package com.changia.changia_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private AppBarConfiguration appBarConfiguration;
    private SessionManager sessionManager;
    private TextView tvUserName, tvUserPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("DEBUG", "=== MAIN ACTIVITY STARTED ===");

        sessionManager = new SessionManager(this);

        setupToolbar();
        setupNavigation();  // CRITICAL: This MUST be called AFTER setContentView
        setupDrawerHeader();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d("DEBUG", "Toolbar setup complete");
    }

    private void setupNavigation() {
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        Log.d("DEBUG", "Starting navigation setup");

        // **FIXED METHOD: Get NavController from NavHostFragment**
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment == null) {
            Log.e("DEBUG", "ERROR: navHostFragment is NULL!");
            return;
        }

        NavController navController = navHostFragment.getNavController();
        Log.d("DEBUG", "NavController obtained successfully");

        // Configure top destinations
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment,
                R.id.groupsFragment,
                R.id.walletFragment,
                R.id.chamaFragment)
                .setOpenableLayout(drawerLayout)
                .build();

        // Setup ActionBar
        try {
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            Log.d("DEBUG", "ActionBar setup complete");
        } catch (Exception e) {
            Log.e("DEBUG", "ActionBar setup failed: " + e.getMessage());
        }

        // Setup Navigation
        try {
            NavigationUI.setupWithNavController(navigationView, navController);
            Log.d("DEBUG", "Drawer navigation setup complete");
        } catch (Exception e) {
            Log.e("DEBUG", "Drawer setup failed: " + e.getMessage());
        }

        // Setup Bottom Navigation
        try {
            NavigationUI.setupWithNavController(bottomNav, navController);
            Log.d("DEBUG", "Bottom navigation setup complete");
        } catch (Exception e) {
            Log.e("DEBUG", "Bottom nav setup failed: " + e.getMessage());
        }

        // Handle drawer menu clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                drawerLayout.closeDrawers();
                return true;
            } else if (id == R.id.nav_logout) {
                logout();
                return true;
            }

            // For other items, let NavigationUI handle it
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) {
                drawerLayout.closeDrawers();
            }
            return handled;
        });

        Log.d("DEBUG", "=== NAVIGATION SETUP COMPLETE ===");
    }

    private void setupDrawerHeader() {
        try {
            NavigationView navigationView = findViewById(R.id.nav_view);
            android.view.View headerView = navigationView.getHeaderView(0);

            tvUserName = headerView.findViewById(R.id.tv_user_name);
            tvUserPhone = headerView.findViewById(R.id.tv_user_phone);

            // Set user info from session
            tvUserName.setText(sessionManager.getUserName());
            tvUserPhone.setText(sessionManager.getUserPhone());

            Log.d("DEBUG", "Drawer header setup complete");
        } catch (Exception e) {
            Log.e("DEBUG", "Drawer header setup failed: " + e.getMessage());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            return NavigationUI.navigateUp(navController, appBarConfiguration)
                    || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }

    private void logout() {
        sessionManager.logout();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}