package ar.com.strellis.ampflower;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import ar.com.strellis.ampflower.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configureNavigation();
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