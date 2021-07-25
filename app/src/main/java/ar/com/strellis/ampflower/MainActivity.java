package ar.com.strellis.ampflower;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import ar.com.strellis.ampflower.data.model.AmpacheAuth;
import ar.com.strellis.ampflower.data.model.AmpacheSettings;
import ar.com.strellis.ampflower.data.model.LoginResponse;
import ar.com.strellis.ampflower.data.model.NetworkStatus;
import ar.com.strellis.ampflower.data.model.ServerStatus;
import ar.com.strellis.ampflower.databinding.ActivityMainBinding;
import ar.com.strellis.ampflower.network.AmpacheService;
import ar.com.strellis.ampflower.network.AmpacheUtil;
import ar.com.strellis.ampflower.network.NetworkReceiver;
import ar.com.strellis.ampflower.viewmodel.NetworkStatusViewModel;
import ar.com.strellis.ampflower.viewmodel.ServerStatusViewModel;
import ar.com.strellis.ampflower.viewmodel.SettingsViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;
    private NetworkStatusViewModel networkStatusViewModel;
    private ServerStatusViewModel serverStatusViewModel;
    private SettingsViewModel settingsViewModel;
    private SharedPreferences.Editor prefsEditor;
    private SharedPreferences sharedPreferences;

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
     * Observes the server status view to display a Toast message whenever the status changes.
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
            if(serverStatus.equals(ServerStatus.UNAVAILABLE))
            {
                Log.d("configureSettingsLoader","The server is in status unavailable and we loaded settings, trying to connect");
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
                                // We're online, update all the status.
                                serverStatusViewModel.setServerStatus(ServerStatus.ONLINE);
                                serverStatusViewModel.setLoginResponse(response.body());
                                Log.d("MainActivity.loginToAmpache","We're in");
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
            }
        }
        else
        {
            Log.d("MainActivity.loginToAmpache","Either the settings are null, or the URL is null, not doing anything");
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
        prefsEditor=sharedPreferences.edit();
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
                if(serverStatus.equals(ServerStatus.UNAVAILABLE))
                {
                    Log.d("configureNetworkStatusListener","The server is unavailable, trying to connect");
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
    }
    private void configureNavigation()
    {
        setSupportActionBar(binding.toolbar);
        binding.layoutMusicPlayer.fabPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
}