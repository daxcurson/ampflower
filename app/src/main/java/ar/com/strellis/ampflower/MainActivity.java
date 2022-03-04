package ar.com.strellis.ampflower;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ar.com.strellis.ampflower.data.model.AmpacheAuth;
import ar.com.strellis.ampflower.data.model.AmpacheError;
import ar.com.strellis.ampflower.data.model.AmpacheSettings;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.NetworkStatus;
import ar.com.strellis.ampflower.data.model.SelectableSong;
import ar.com.strellis.ampflower.data.model.ServerStatus;
import ar.com.strellis.ampflower.data.model.Song;
import ar.com.strellis.ampflower.data.repository.AlbumsRepositoryRx;
import ar.com.strellis.ampflower.data.repository.ArtistsRepository;
import ar.com.strellis.ampflower.data.repository.ArtistsRepositoryRx;
import ar.com.strellis.ampflower.data.repository.PlaylistsRepository;
import ar.com.strellis.ampflower.data.repository.SongsRepository;
import ar.com.strellis.ampflower.databinding.ActivityMainBinding;
import ar.com.strellis.ampflower.networkutils.AmpacheService;
import ar.com.strellis.ampflower.networkutils.AmpacheUtil;
import ar.com.strellis.ampflower.networkutils.NetworkReceiver;
import ar.com.strellis.ampflower.service.MediaPlayerService;
import ar.com.strellis.ampflower.service.MediaServiceEventsListener;
import ar.com.strellis.ampflower.service.PlayerPositionEvent;
import ar.com.strellis.ampflower.viewmodel.AlbumsViewModel;
import ar.com.strellis.ampflower.viewmodel.ArtistsViewModel;
import ar.com.strellis.ampflower.viewmodel.FavoritesViewModel;
import ar.com.strellis.ampflower.viewmodel.NetworkStatusViewModel;
import ar.com.strellis.ampflower.viewmodel.PlayerViewModel;
import ar.com.strellis.ampflower.viewmodel.PlaylistsViewModel;
import ar.com.strellis.ampflower.viewmodel.ServerStatusViewModel;
import ar.com.strellis.ampflower.viewmodel.SettingsViewModel;
import ar.com.strellis.ampflower.viewmodel.SongsViewModel;
import ar.com.strellis.utils.SlidingUpPanelLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MediaServiceEventsListener
{

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;
    private NetworkStatusViewModel networkStatusViewModel;
    private ServerStatusViewModel serverStatusViewModel;
    private AlbumsViewModel albumsViewModel;
    private ArtistsViewModel artistsViewModel;
    private SongsViewModel songsViewModel;
    private PlaylistsViewModel playlistsViewModel;
    private SettingsViewModel settingsViewModel;
    private PlayerViewModel playerViewModel;
    private FavoritesViewModel favoritesViewModel;
    private SharedPreferences sharedPreferences;
    private boolean boundToService=false;
    private MediaPlayerService playerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getPreferences(MODE_PRIVATE);
        configureNavigation();
        configureDataViewModel();
        configureStatusDisplay();
        configureInitialState();
        configureSettingsLoader();
        configureNetworkStatusListener();
        configureSettingsObserver();
        configureServerStatusObserver();
        configureButtons();
        bindMediaPlayerService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
    private void showToast(String message)
    {
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(getApplicationContext(), message, duration);
        toast.show();
    }

    /**
     * Observes the server status view to trigger the load of the data repositories
     */
    private void configureServerStatusObserver()
    {
        Observer<ServerStatus> serverStatusObserver=status->{
            switch(Objects.requireNonNull(status))
            {
                case UNAVAILABLE:
                    Log.d("MainActivity.configuraServerStatusObserver","The server is now unavailable");
                    showToast("The server is unavailable");
                    break;
                case LOGIN_DENIED:
                    Log.d("MainActivity.configuraServerStatusObserver","The server is now unavailable");
                    showToast("Login denied");
                    break;
                case ONLINE:
                    // If the server is online, it may be the first time, but it can also be after staying
                    // offline, for example, if the authentication token expired and we need to renew it.
                    // By the time we get here, the token has been renewed since the change in server status
                    // is done from the login method.
                    Log.d("MainActivity.configuraServerStatusObserver","The server is now unavailable");
                    showToast("We're online!");
                    configureDataModels();
                    renewPlaylist();
                    break;
            }
        };
        serverStatusViewModel.getServerStatus().observe(this,serverStatusObserver);
    }

    /**
     * Configures the initial server status which is unavailable
     */
    private void configureInitialState()
    {
        // We'll configure the initial state, which is, server unavailable.
        serverStatusViewModel.setServerStatus(ServerStatus.UNAVAILABLE);
        // setting stopped mode
        if(playerViewModel.getPlayerState().getValue()!=null)
        {
            Log.d("MainActivity","I have a previous state");
            State state = playerViewModel.getPlayerState().getValue();
            if(state.name.equals("PLAYING"))
            {
                Log.d("MainActivity","The state is playing, media item: "+ Objects.requireNonNull(playerViewModel.getMediaItem().getValue()).mediaMetadata.title);
                updateUiWithMediaItem(Objects.requireNonNull(playerViewModel.getMediaItem().getValue()));
                updateUiToPlayState();
                this.state=playing;
            }
            else
            {
                this.state=stopped;
                playerViewModel.setPlayerState(stopped);
            }
        }
        else
        {
            state=stopped;
            playerViewModel.setPlayerState(stopped);
        }
    }

    /**
     * Listens for the change in the server status to update the menu icon
     */
    private void configureStatusDisplay()
    {
        // This changes the cloud icon to display the different server status.
        Observer<ServerStatus> serverStatusObserver=status->{
            Log.d("configureStatusDisplay","Observing the change in server status");
            // We need to invalidate the options menu, to force the call to onPrepareOptionsMenu(),
            // thus, we have an opportunity to modify the icon according to the server status.
            invalidateOptionsMenu();
        };
        serverStatusViewModel.getServerStatus().observe(this,serverStatusObserver);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem serverStatusMenuItem=menu.findItem(R.id.action_server_status);
        // Now, with the menu item on hand, update its icon.
        ServerStatus serverStatus=serverStatusViewModel.getServerStatus().getValue();
        switch(Objects.requireNonNull(serverStatus))
        {
            case UNAVAILABLE:
                serverStatusMenuItem.setIcon(R.drawable.ic_cloud_unavail);
                break;
            case LOGIN_DENIED:
                serverStatusMenuItem.setIcon(R.drawable.ic_cloud_cross);
                break;
            case CONNECTING:
                serverStatusMenuItem.setIcon(R.drawable.ic_cloud_loading);
                break;
            case ONLINE:
                serverStatusMenuItem.setIcon(R.drawable.ic_cloud_checked);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Creates a listener for the settings, that will check the server status, and if it is unavailable, it will try
     * to connect
     */
    private void configureSettingsLoader()
    {
        // We'll create an observer for the settings loader that will try to connect to the server.
        Observer<AmpacheSettings> loadedSettingsObserver=settings->{
            Log.d("configureSettingsLoader","Loaded settings");
            ServerStatus serverStatus=serverStatusViewModel.getServerStatus().getValue();
            assert serverStatus != null;
            if(!serverStatus.equals(ServerStatus.CONNECTING) && !serverStatus.equals(ServerStatus.LOGIN_DENIED) && !serverStatus.equals(ServerStatus.ONLINE))
            {
                Log.d("configureSettingsLoader","The server is not connecting, we are not denied and not online and we loaded settings, trying to connect");
                loginToAmpache();
            }
            else
            {
                Log.d("configureSettingsLoader","The server is in the state "+serverStatus.name()+", I won't try to connect");
            }
        };
        serverStatusViewModel.getAmpacheSettings().observe(this,loadedSettingsObserver);
        serverStatusViewModel.setAmpacheSettings(loadSavedSettings());
    }
    private synchronized void loginToAmpache()
    {
        Log.d("MainActivity.loginToAmpache","Attempting to log in");
        serverStatusViewModel.setServerStatus(ServerStatus.CONNECTING);
        AmpacheSettings settings=serverStatusViewModel.getAmpacheSettings().getValue();
        if(settings!=null
                && settings.getAmpacheUrl()!=null
                && !settings.getAmpacheUrl().equals("")) {
            AmpacheService ampacheService= AmpacheUtil.getService(settings);
            String user = settings.getAmpacheUsername();
            String password = settings.getAmpachePassword();
            // We attempt to log in only if we actually have data in those fields
            if(user!=null && !user.equals("") && password!=null && !password.equals("")) {
                AmpacheAuth auth;
                try {
                    auth = AmpacheUtil.getAmpacheAuth(password);
                    Call<LoginResponse> call = ampacheService.handshake(auth.getAuthToSend(), user, auth.getTimestamp());

                    call.enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(@NotNull Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {
                            if (response.body() != null) {
                                if(response.body().getAuth()!=null) {
                                    int duration = Toast.LENGTH_LONG;
                                    Toast toast = Toast.makeText(getApplicationContext(), "Login successful", duration);
                                    toast.show();
                                    Log.d("MainActivity.loginToAmpache", "We're in");
                                    serverStatusViewModel.setLoginResponse(response.body());
                                    serverStatusViewModel.setServerStatus(ServerStatus.ONLINE);
                                }
                                else
                                {
                                    // Error!
                                    AmpacheError error=response.body().getError();
                                    Log.d("loginToAmpache.onFailure","Failed to log in: "+error.getErrorMessage()+ "("+error.getErrorCode()+")");
                                    serverStatusViewModel.setServerStatus(ServerStatus.UNAVAILABLE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<LoginResponse> call, @NotNull Throwable t) {
                            Log.d("loginToAmpache.onFailure","Failed to log in: "+t.getMessage());
                            serverStatusViewModel.setServerStatus(ServerStatus.UNAVAILABLE);
                        }
                    });
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Log.d("MainActivity.loginToAmpache","The required parameters user, password and url are blank, not attempting connection");
                serverStatusViewModel.setServerStatus(ServerStatus.UNAVAILABLE);
            }
        }
        else
        {
            Log.d("MainActivity.loginToAmpache","Either the settings are null, or the URL is null, not doing anything");
            serverStatusViewModel.setServerStatus(ServerStatus.UNAVAILABLE);
        }
    }

    /**
     * Configures an observer for the settings, that will try to save them if they change
     */
    private void configureSettingsObserver()
    {
        Observer<AmpacheSettings> settingsObserver=settings->{
            Log.d("configureSettingsObserver","Settings changed, saving");
            saveSettings(settings);
        };
        settingsViewModel.getAmpacheSettings().observe(this,settingsObserver);
    }

    /**
     * Loads settings
     * @return settings loaded from the shared preferences
     */
    private AmpacheSettings loadSavedSettings()
    {
        Gson gson=new Gson();
        String serializedPreferences=sharedPreferences.getString("settings","");
        AmpacheSettings settings=gson.fromJson(serializedPreferences,AmpacheSettings.class);
        if(settings==null)
            settings=new AmpacheSettings();
        return settings;
    }

    /**
     * Saves settings
     * @param settings The settings object to be saved
     */
    @SuppressLint("ApplySharedPref")
    private void saveSettings(AmpacheSettings settings)
    {
        Log.d("saveSettings","About to save settings");
        Gson gson=new Gson();
        String serializedPreferences=gson.toJson(settings);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString("settings",serializedPreferences);
        prefsEditor.commit();
    }

    /**
     * Configures a listener for the network status change, that will attempt to connect to the server
     * if its status is unavailable
     */
    private void configureNetworkStatusListener()
    {
        Observer<NetworkStatus> networkStatusObserver=networkStatus -> {
            Log.d("configureNetworkStatusListener","Network state changed");
            if(networkStatus.equals(NetworkStatus.ONLINE))
            {
                Log.d("configureNetworkStatusListener","Network status is online");
                ServerStatus serverStatus=serverStatusViewModel.getServerStatus().getValue();
                assert serverStatus != null;
                if(!serverStatus.equals(ServerStatus.CONNECTING) && !serverStatus.equals(ServerStatus.LOGIN_DENIED) && !serverStatus.equals(ServerStatus.ONLINE))
                {
                    Log.d("configureNetworkStatusListener","The server is not connecting, we are not denied and not online, trying to connect");
                    loginToAmpache();
                }
                else
                {
                    Log.d("configureNetworkStatusListener","The server is in the status "+serverStatus.name()+", I won't try to log in");
                }
            }
        };
        networkStatusViewModel.getNetworkStatus().observe(this,networkStatusObserver);
        NetworkReceiver networkReceiver=new NetworkReceiver(getApplicationContext(),networkStatusViewModel);
        networkReceiver.registerNetworkCallback();
    }
    private void configureDataViewModel()
    {
        networkStatusViewModel=new ViewModelProvider(this).get(NetworkStatusViewModel.class);
        serverStatusViewModel=new ViewModelProvider(this).get(ServerStatusViewModel.class);
        settingsViewModel=new ViewModelProvider(this).get(SettingsViewModel.class);
        albumsViewModel=new ViewModelProvider(this).get(AlbumsViewModel.class);
        artistsViewModel=new ViewModelProvider(this).get(ArtistsViewModel.class);
        playlistsViewModel=new ViewModelProvider(this).get(PlaylistsViewModel.class);
        songsViewModel=new ViewModelProvider(this).get(SongsViewModel.class);
        playerViewModel=new ViewModelProvider(this).get(PlayerViewModel.class);
        favoritesViewModel=new ViewModelProvider(this).get(FavoritesViewModel.class);
    }
    private void configureDataModels()
    {
        configureAlbumsViewModel();
        configureArtistsViewModel();
        configurePlaylistsViewModel();
        configureSongsViewModel();
        configureFavoritesViewModel();
    }
    private void configureAlbumsViewModel()
    {
        AmpacheSettings settings=serverStatusViewModel.getAmpacheSettings().getValue();
        assert settings != null;
        AmpacheService networkService= AmpacheUtil.getService(settings);
        LoginResponse loginResponse=serverStatusViewModel.getLoginResponse().getValue();
        AlbumsRepositoryRx albumRepository = AlbumsRepositoryRx.getInstance(this,networkService, settings,loginResponse,albumsViewModel.getQuery(),this);
        albumsViewModel.setAlbumsRepository(albumRepository);
    }
    private void configureArtistsViewModel()
    {
        AmpacheSettings settings=serverStatusViewModel.getAmpacheSettings().getValue();
        assert settings != null;
        AmpacheService service= AmpacheUtil.getService(settings);
        LoginResponse loginResponse=serverStatusViewModel.getLoginResponse().getValue();
        ArtistsRepositoryRx artistsRepository = ArtistsRepositoryRx.getInstance(this,service, settings,loginResponse,artistsViewModel.getQuery(),this);
        artistsViewModel.setArtistsRepository(artistsRepository);
    }
    private void configurePlaylistsViewModel()
    {
        AmpacheSettings settings=serverStatusViewModel.getAmpacheSettings().getValue();
        assert settings != null;
        AmpacheService service= AmpacheUtil.getService(settings);
        LoginResponse loginResponse=serverStatusViewModel.getLoginResponse().getValue();
        PlaylistsRepository playlistsRepository = PlaylistsRepository.getInstance(this,service, settings,loginResponse);
        playlistsViewModel.setPlaylistsRepository(playlistsRepository);
    }
    private void configureSongsViewModel()
    {
        AmpacheSettings settings=serverStatusViewModel.getAmpacheSettings().getValue();
        assert settings != null;
        AmpacheService service=AmpacheUtil.getService(settings);
        LiveData<LoginResponse> loginResponse=serverStatusViewModel.getLoginResponse();
        SongsRepository songsRepository=new SongsRepository(this,service,loginResponse);
        songsViewModel.setSongsRepository(songsRepository);
    }
    private void configureFavoritesViewModel()
    {
    }
    private void configureNavigation()
    {
        setSupportActionBar(binding.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        DrawerLayout drawerLayout=findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,binding.toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavHostFragment navHostFragment= (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController=navHostFragment.getNavController();
        appBarConfiguration = new AppBarConfiguration.Builder(
                navController.getGraph())
                .setOpenableLayout(drawer)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);

    }

    /**
     * Sets the observers for the buttons.
     */
    private void configureButtons()
    {
        binding.layoutMusicPlayer.fabPlay.setOnClickListener(view -> {
            Intent intent=getMediaPlayerServiceIntent();
            intent.setAction(MediaPlayerService.ACTION_TOGGLE);
            startService(intent);
        });
        binding.layoutMusicPlayer.imgPlayerPlay.setOnClickListener(view ->{
            Intent intent=getMediaPlayerServiceIntent();
            intent.setAction(MediaPlayerService.ACTION_TOGGLE);
            startService(intent);
        });
        binding.layoutMusicPlayer.imgPlayerNext.setOnClickListener(view ->{
            Intent intent=getMediaPlayerServiceIntent();
            intent.setAction(MediaPlayerService.ACTION_NEXT);
            startService(intent);
        });
        binding.layoutMusicPlayer.imgPlayerPrevious.setOnClickListener(view->{
            Intent intent=getMediaPlayerServiceIntent();
            intent.setAction(MediaPlayerService.ACTION_PREVIOUS);
            startService(intent);
        });
        binding.layoutMusicPlayer.imgNextExpand.setOnClickListener(view->{
            Intent intent=getMediaPlayerServiceIntent();
            intent.setAction(MediaPlayerService.ACTION_NEXT);
            startService(intent);
        });
        binding.layoutMusicPlayer.imgPreviousExpand.setOnClickListener(view->{
            Intent intent=getMediaPlayerServiceIntent();
            intent.setAction(MediaPlayerService.ACTION_PREVIOUS);
            startService(intent);
        });
        binding.layoutMusicPlayer.imgPlaylist.setOnClickListener(view->{
            // This shows the playlist fragment. But first I need to collapse the player.
            binding.slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            // Now, navigate to the new view.
            navController.navigate(R.id.nav_view_playlist);
        });
        binding.layoutMusicPlayer.seekBarSong.addListener(new TimeBar.OnScrubListener() {
            @Override
            public void onScrubStart(TimeBar timeBar, long position) {
                // Not interested in this, yet
            }

            @Override
            public void onScrubMove(TimeBar timeBar, long position) {
                // Not interested in this.
            }

            @Override
            public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
                // Interested in this. I'm going to ask it for its position and send a message to the
                // MediaPlayerService to change its position
                Log.d("MainActivity","Seeking to position "+position);
                setPositionInSong(position);
            }
        });
    }
    private Intent getMediaPlayerServiceIntent()
    {
        return new Intent(this,MediaPlayerService.class);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_server_status)
        {
            navController.navigate(R.id.nav_server_status);
        }
        if(id==R.id.action_settings)
        {
            // Settings
        }
        return super.onOptionsItemSelected(item);
    }

        @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.nav_settings)
        {
            binding.drawerLayout.closeDrawers();
            callSettingsFragment();
        }
        if(id==R.id.nav_library)
        {
            binding.drawerLayout.closeDrawers();
            callLibraryFragment();
        }
        if(id==R.id.nav_home)
        {
            binding.drawerLayout.closeDrawers();
            callHomeFragment();
        }
        return true;
    }
    private void callSettingsFragment()
    {
        navController.navigate(R.id.nav_settings);
    }
    private void callLibraryFragment()
    {
        navController.navigate(R.id.nav_library);
    }
    private void callHomeFragment()
    {
        navController.navigate(R.id.nav_home);
    }

    private final ServiceConnection connection=new ServiceConnection()
    {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerService.MediaPlayerServiceBinder binder=(MediaPlayerService.MediaPlayerServiceBinder) service;
            playerService=binder.getService();
            // Now I'll ask the player service to send me events.
            if(!boundToService)
                playerService.addEventListener(MainActivity.this);
            boundToService=true;
            Log.d("DEBUG","Connected to the service");
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            boundToService=false;
            playerService.removeEventListener(MainActivity.this);
            playerService=null;
            Log.d("DEBUG","Disconnected from the service");
        }

        @Override
        public void onBindingDied(ComponentName name) {
            Log.d("DEBUG","Binding died, component name: "+name.getClassName());
        }

        @Override
        public void onNullBinding(ComponentName name) {
            Log.d("DEBUG","Null binding, component name:"+name.getClassName());
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // I need to remove myself as listener to the service!
        playerService.removeEventListener(this);
        Log.d("MainActivity","I'm destroyed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,new IntentFilter(BuildConfig.APPLICATION_ID+".action.RENEW_LOGINRESPONSE"));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onPause();
    }

    private void bindMediaPlayerService()
    {
        if(!boundToService)
            bindService(MediaPlayerService.newIntent(this),connection, Context.BIND_AUTO_CREATE);
    }
    private final BroadcastReceiver messageReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BroadcastReceiver.onReceive", "Received a broadcast message!");
            if(intent.getAction().equals(MediaPlayerService.ACTION_RENEW_TOKEN)) {
                Log.d("BroadcastReceiver.onReceive", "Received a request to renew the login response");
                loginToAmpache();
                // And apart from logging in, we need to destroy the Artist Repositories, but they are Singletons!
                // We'll pass the session token, and let them decide if they need to be destroyed.
                configureDataModels();
            }
        }
    };

    public abstract static class State
    {
        private String name;
        public State(String name)
        {
            this.name=name;
        }
        public void setName(String name)
        {
            this.name=name;
        }
        public String getName()
        {
            return this.name;
        }
        public abstract void play(MediaItem item);
        public abstract void pause();
    }
    public class Playing extends State
    {
        public Playing()
        {
            super("PLAYING");
        }

        @Override
        public void play(MediaItem item) {
            assert item.playbackProperties != null;
            Log.d("Playing","Received a Play message, looping into playing, URL of the media item: "+item.playbackProperties.uri);
            updateUiWithMediaItem(item);
            updateUiToPlayState();
            state=playing;
            playerViewModel.setPlayerState(playing);
            playerViewModel.setMediaItem(item);
        }

        @Override
        public void pause() {
            updateUiToPausedState();
            state=stopped;
            playerViewModel.setPlayerState(stopped);
        }
    }
    public class Stopped extends State
    {
        public Stopped()
        {
            super("STOPPED");
        }
        @Override
        public void play(MediaItem item)
        {
            Log.d("Stopped","Received a Play message, changing to Play");
            updateUiWithMediaItem(item);
            updateUiToPlayState();
            state=playing;
            playerViewModel.setPlayerState(playing);
            playerViewModel.setMediaItem(item);
        }
        @Override
        public void pause()
        {
            updateUiToPausedState();
            state=stopped;
            playerViewModel.setPlayerState(stopped);
        }
    }

    /**
     * Changes the shapes of the controls to represent the state of playing music
     */
    private void updateUiToPlayState()
    {
        binding.layoutMusicPlayer.imgPlayerPlay.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this,R.drawable.ic_pause_white));
        binding.layoutMusicPlayer.fabPlay.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this,R.drawable.ic_pause_white));
    }

    /**
     * Changes the shapes of the controls to represent the state of being paused
     */
    private void updateUiToPausedState()
    {
        binding.layoutMusicPlayer.imgPlayerPlay.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this,R.drawable.ic_play_arrow_white));
        binding.layoutMusicPlayer.fabPlay.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this,R.drawable.ic_play_arrow_white));
    }

    /**
     * Updates the images and texts with the contents of the metadata of the media item
     * @param item the item being played
     */
    private void updateUiWithMediaItem(MediaItem item)
    {
        binding.layoutMusicPlayer.txtSongName.setText(item.mediaMetadata.title);
        binding.layoutMusicPlayer.txtMetadata.setText(item.mediaMetadata.artist);
        binding.layoutMusicPlayer.txtSongNameExpand.setText(item.mediaMetadata.title);
        binding.layoutMusicPlayer.txtSongMetadataExpand.setText(item.mediaMetadata.artist);
        Picasso.get().load(item.mediaMetadata.artworkUri).into(binding.layoutMusicPlayer.imgCoverLarge);
        Picasso.get().load(item.mediaMetadata.artworkUri).into(binding.layoutMusicPlayer.imgCoverSmall);
        // It seems that I can get the art of albums, artists, songs, if I know
        // the URL for it... let's try!
        Bundle extras=item.mediaMetadata.extras;
        if(extras!=null) {
            int artistId = extras.getInt("ARTIST_ID");
            String url=artistsViewModel.getArtistsRepository().getImageUrl(artistId);
            Log.d("MainActivity","Loading artist art: "+url);
            Picasso.get().load(url).into(binding.layoutMusicPlayer.imgMusicBackground);
        }
    }
    private State state;
    private final Playing playing=new Playing();
    private final Stopped stopped=new Stopped();
    @Override
    public void setBuffering(boolean status) {
        Log.d("MainActivity","Received a buffering message");
    }

    @Override
    public void setPlaying(MediaItem item,int positionInPlaylist) {
        Log.d("MainActivity","Received a playing message, to play item "+positionInPlaylist);
        state.play(item);
    }

    @Override
    public void setPaused() {
        Log.d("MainActivity","Received a Paused message");
        state.pause();
    }

    @Override
    public void updateProgress(PlayerPositionEvent position) {
        if(position.getDuration()>=0)
        {
            Log.d("MainActivity", "Received a progress update, position: " + position.getPosition() + ", duration: " + position.getDuration());
            binding.layoutMusicPlayer.seekBarSong.setDuration(position.getDuration());//.setMax((int)position.getDuration());
            binding.layoutMusicPlayer.seekBarSong.setPosition(position.getPosition());//.setProgress((int) position.getPosition());
            binding.layoutMusicPlayer.seekBarSong.setBufferedPosition(position.getBufferedPosition());
            // Update the text boxes with the time values
            long minutesDuration=(position.getDuration()/1000)/60;
            long secondsDuration=(position.getDuration()/1000)%60;
            long minutesPosition=(position.getPosition()/1000)/60;
            long secondsPosition=(position.getPosition()/1000)%60;
            binding.layoutMusicPlayer.txtTotalDuration.setText(getString(R.string.duration,minutesDuration,secondsDuration));
            binding.layoutMusicPlayer.txtSongDuration.setText(getString(R.string.currentTime,minutesPosition,secondsPosition));
            playerViewModel.setCurrentPosition(position.getPosition());
        }
        else
            Log.d("MainActivity","Received a progress update, position: "+position.getPosition()+", duration: "+position.getDuration()+", avoiding updating the progress");
    }

    @Override
    public void songChanged(MediaItem item, int positionInPlaylist) {
        Log.d("MainActivity","Received a SongChanged message, updating UI");
        updateUiWithMediaItem(item);
        // and now, the all-important notification about the media item number being played.
        songsViewModel.setCurrentItemInPlaylist(positionInPlaylist);
    }

    /**
     * If the server comes back online after, say, the login token is renewed, and there are songs in the playlist,
     * we need to renew the token, and for that, we need to either edit the URL of the song to replace the token,
     * or get the song again from the server. We'll try doing the former, i.e.,
     */
    private void renewPlaylist()
    {
        List<SelectableSong> currentPlaylist=songsViewModel.getCurrentPlaylist().getValue();
        LoginResponse loginResponse=serverStatusViewModel.getLoginResponse().getValue();
        if(currentPlaylist!=null && !currentPlaylist.isEmpty()) {
            List<SelectableSong> newPlaylist=new LinkedList<>();
            for (SelectableSong s : currentPlaylist) {
                Uri oldUri = Uri.parse(s.getSong().getUrl());
                Uri.Builder uriBuilder = new Uri.Builder()
                        .scheme(oldUri.getScheme())
                        .authority(oldUri.getAuthority())
                        .path(oldUri.getPath());
                for (String parameter : oldUri.getQueryParameterNames()) {
                    // Now, for each parameter, if it is the ssid, replace it.
                    if (parameter.equals("ssid") && loginResponse != null && loginResponse.getAuth() != null) {
                        uriBuilder.appendQueryParameter("ssid", loginResponse.getAuth());
                    } else // otherwise, pass through the old value
                    {
                        uriBuilder.appendQueryParameter(parameter, oldUri.getQueryParameter(parameter));
                    }
                }
                Uri newUri = uriBuilder.build();
                s.getSong().setUrl(newUri.toString());
                // Now, add the song to the new playlist.
                newPlaylist.add(s);
            }
            // I have all the songs. It's time to tell the player to re-add them.
            restorePlayerStatus(newPlaylist);
        }
    }

    /**
     * Informs the server to add the songs to the list, and if the player is playing,
     * restore the position and the seek time.
     * @param songs
     */
    private void restorePlayerStatus(List<SelectableSong> songs)
    {
        Log.d("MainActivity.restorePlayerStatus","Replacing currently-selected songs");
        songsViewModel.setCurrentPlaylist(songs);
        Log.d("MainActivity.restorePlayerStatus","Restoring player status after refreshing the token");
        sendCurrentPlaylist();
        // Notify playlist item
        if(songsViewModel.getCurrentItemInPlaylist()!=null && songsViewModel.getCurrentItemInPlaylist().getValue()!=null) {
            playPlaylistItem(songsViewModel.getCurrentItemInPlaylist().getValue());
            if(playerViewModel.getCurrentPosition()!=null && playerViewModel.getCurrentPosition().getValue()!=null)
                setPositionInSong(playerViewModel.getCurrentPosition().getValue());
        }
    }
    public void sendCurrentPlaylist()
    {
        // Send songs
        Intent intent=new Intent(MainActivity.this, MediaPlayerService.class);
        intent.setAction(MediaPlayerService.ACTION_SELECT_SONGS);
        List<SelectableSong> selectedSongs=songsViewModel.getCurrentPlaylist().getValue();
        // I need to send a list of Song instead of SelectableSong...
        if(selectedSongs!=null && !selectedSongs.isEmpty()) {
            List<Song> songsToSend = selectedSongs.stream().map(
                    SelectableSong::getSong
            ).collect(Collectors.toList());
            Bundle bundle = new Bundle();
            bundle.putSerializable("LIST", (Serializable) songsToSend);
            intent.putExtras(bundle);
            startService(intent);
        }
    }
    public void sendSelectedSongsToPlayList()
    {
        // Send songs. If there are too many songs to send, the list
        // will have to be split, sending a different intent
        Intent intent=new Intent(MainActivity.this, MediaPlayerService.class);
        intent.setAction(MediaPlayerService.ACTION_SELECT_SONGS);
        List<Song> selectedSongs=songsViewModel.getSelectedSongs();
        Bundle bundle=new Bundle();
        bundle.putSerializable("LIST", (Serializable) selectedSongs);
        intent.putExtras(bundle);
        startService(intent);
    }

    /**
     * Tells the media service to play the specified item in the current playlist
     * @param position 0-indexed Item number in the playlist
     */
    public void playPlaylistItem(int position)
    {
        Intent intent=new Intent(this, MediaPlayerService.class);
        intent.setAction(MediaPlayerService.ACTION_PLAY_ITEM);
        Bundle bundle=new Bundle();
        bundle.putSerializable("position",position);
        intent.putExtras(bundle);
        startService(intent);
    }

    /**
     * Tells the media service to seek the currently-played song to the specified position
     * @param position Position within the song to seek to
     */
    public void setPositionInSong(long position)
    {
        songsViewModel.setCurrentPlaylist(songsViewModel.getSelectedSongsIntoPlaylist());
        Intent intent=new Intent(MainActivity.this, MediaPlayerService.class);
        intent.setAction(MediaPlayerService.ACTION_SEEK);
        Bundle bundle=new Bundle();
        bundle.putSerializable("position",position);
        intent.putExtras(bundle);
        startService(intent);
    }
}