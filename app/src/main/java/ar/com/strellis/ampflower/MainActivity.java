package ar.com.strellis.ampflower;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import ar.com.strellis.ampflower.data.model.AmpacheAuth;
import ar.com.strellis.ampflower.data.model.AmpacheSettings;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.NetworkStatus;
import ar.com.strellis.ampflower.data.model.ServerStatus;
import ar.com.strellis.ampflower.data.repository.AlbumsRepository;
import ar.com.strellis.ampflower.data.repository.ArtistsRepository;
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
import ar.com.strellis.ampflower.viewmodel.NetworkStatusViewModel;
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
        this.state=stopped;
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
                    showToast("The server is unavailable");
                    break;
                case LOGIN_DENIED:
                    showToast("Login denied");
                    break;
                case ONLINE:
                    showToast("We're online!");
                    configureDataModels();
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
                Log.d("configureSettingsLoader","The server is not connectiong, we are not denied and not online and we loaded settings, trying to connect");
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
                                int duration = Toast.LENGTH_LONG;
                                Toast toast = Toast.makeText(getApplicationContext(), "Login successful", duration);
                                toast.show();
                                Log.d("MainActivity.loginToAmpache","We're in");
                                serverStatusViewModel.setLoginResponse(response.body());
                                serverStatusViewModel.setServerStatus(ServerStatus.ONLINE);
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
                    Log.d("configureNetworkStatusListener","The server is not connectiong, we are not denied and not online, trying to connect");
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
    }
    private void configureDataModels()
    {
        configureAlbumsViewModel();
        configureArtistsViewModel();
        configurePlaylistsViewModel();
        configureSongsViewModel();
    }
    private void configureAlbumsViewModel()
    {
        AmpacheSettings settings=serverStatusViewModel.getAmpacheSettings().getValue();
        assert settings != null;
        AmpacheService networkService= AmpacheUtil.getService(settings);
        LoginResponse loginResponse=serverStatusViewModel.getLoginResponse().getValue();
        AlbumsRepository albumRepository = AlbumsRepository.getInstance(this,networkService, settings,loginResponse,albumsViewModel.getQuery(),this);
        albumsViewModel.setAlbumsRepository(albumRepository);
    }
    private void configureArtistsViewModel()
    {
        AmpacheSettings settings=serverStatusViewModel.getAmpacheSettings().getValue();
        assert settings != null;
        AmpacheService service= AmpacheUtil.getService(settings);
        LoginResponse loginResponse=serverStatusViewModel.getLoginResponse().getValue();
        ArtistsRepository artistsRepository = ArtistsRepository.getInstance(this,service, settings,loginResponse,artistsViewModel.getQuery(),this);
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
            Navigation.findNavController(view).navigate(R.id.nav_view_playlist);
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
    private void bindMediaPlayerService()
    {
        if(!boundToService)
            bindService(MediaPlayerService.newIntent(this),connection, Context.BIND_AUTO_CREATE);
    }

    private abstract static class State
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
    private class Playing extends State
    {
        public Playing()
        {
            super("PLAYING");
        }

        @Override
        public void play(MediaItem item) {
            state=playing;
        }

        @Override
        public void pause() {
            binding.layoutMusicPlayer.imgPlayerPlay.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this,R.drawable.ic_play_arrow_white));
            binding.layoutMusicPlayer.fabPlay.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this,R.drawable.ic_play_arrow_white));
            state=stopped;
        }
    }
    private class Stopped extends State
    {
        public Stopped()
        {
            super("STOPPED");
        }
        @Override
        public void play(MediaItem item)
        {
            binding.layoutMusicPlayer.imgPlayerPlay.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this,R.drawable.ic_pause_white));
            binding.layoutMusicPlayer.fabPlay.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this,R.drawable.ic_pause_white));
            binding.layoutMusicPlayer.txtSongName.setText(item.mediaMetadata.title);
            binding.layoutMusicPlayer.txtMetadata.setText(item.mediaMetadata.artist);
            binding.layoutMusicPlayer.txtSongNameExpand.setText(item.mediaMetadata.title);
            binding.layoutMusicPlayer.txtSongMetadataExpand.setText(item.mediaMetadata.artist);
            Picasso.get().load(item.mediaMetadata.artworkUri).into(binding.layoutMusicPlayer.imgCoverLarge);
            binding.layoutMusicPlayer.imgCollapse.setImageBitmap(binding.layoutMusicPlayer.imgCoverLarge.getDrawingCache());
            state=playing;
        }
        @Override
        public void pause()
        {
            state=stopped;
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
    public void setPlaying(MediaItem item) {
        Log.d("MainActivity","Received a playing message");
        state.play(item);
    }

    @Override
    public void setPaused() {
        Log.d("MainActivity","Received a Paused message");
        state.pause();
    }

    @Override
    public void updateProgress(PlayerPositionEvent position) {
        Log.d("MainActivity","Received a progress update, position: "+position.getPosition()+", duration: "+position.getDuration());
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
    }
}