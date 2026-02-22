package com.changia.changia_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData; // Import LiveData
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
    private BottomNavigationView bottomNav;

    // --- FIX: Add AppDatabase ---
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("DEBUG", "=== MAIN ACTIVITY STARTED ===");

        sessionManager = new SessionManager(this);
        // --- FIX: Initialize the database ---
        appDatabase = AppDatabase.getDatabase(this);

        setupToolbar();
        setupNavigation();
        // --- FIX: This method will now use LiveData ---
        observeDrawerHeaderData();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d("DEBUG", "Toolbar setup complete");
    }

    private void setupNavigation() {
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        bottomNav = findViewById(R.id.bottom_nav);

        Log.d("DEBUG", "Starting navigation setup");

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment == null) {
            Log.e("DEBUG", "ERROR: navHostFragment is NULL!");
            return;
        }

        NavController navController = navHostFragment.getNavController();
        Log.d("DEBUG", "NavController obtained successfully");

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment,
                R.id.groupsFragment,
                R.id.walletFragment,
                R.id.chamaFragment)
                .setOpenableLayout(drawerLayout)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNav, navController);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                // In a real app, this might navigate to a profile fragment
                // startActivity(new Intent(this, ProfileActivity.class));
                drawerLayout.closeDrawers();
                return true;
            } else if (id == R.id.nav_logout) {
                logout();
                return true;
            }

            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) {
                drawerLayout.closeDrawers();
            }
            return handled;
        });

        Log.d("DEBUG", "=== NAVIGATION SETUP COMPLETE ===");
    }

    /**
     * --- THIS IS THE UPGRADED METHOD ---
     * It observes the UserEntity from the database using LiveData.
     * When the user's data changes, the drawer header will update automatically.
     */
    private void observeDrawerHeaderData() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        android.view.View headerView = navigationView.getHeaderView(0);
        TextView tvUserName = headerView.findViewById(R.id.tv_user_name);
        TextView tvUserPhone = headerView.findViewById(R.id.tv_user_phone);

        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Log.e("DEBUG", "Cannot observe user data: Invalid user ID.");
            // Set fallback data
            tvUserName.setText("Guest");
            tvUserPhone.setText("Not logged in");
            return;
        }

        // Get the LiveData object for the current user
        LiveData<UserEntity> userLiveData = appDatabase.userDao().getUserByIdLive(userId);

        // Observe the LiveData for changes
        userLiveData.observe(this, userEntity -> {
            if (userEntity != null) {
                Log.d("DEBUG", "Drawer header data updated for user: " + userEntity.getFullName());
                tvUserName.setText(userEntity.getFullName());

                String phone = userEntity.getPhoneNumber();
                // Use phone number if available, otherwise fall back to email
                if (phone != null && !phone.isEmpty()) {
                    tvUserPhone.setText(phone);
                } else {
                    tvUserPhone.setText(userEntity.getEmail());
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void logout() {
        sessionManager.logoutUser();
        // The logoutUser method in SessionManager already handles navigation,
        // but an explicit finish() here is a good safety measure.
        finish();
    }

    /**
     * Navigate to Groups tab programmatically
     * Called from HomeFragment when "View All" is clicked
     */
    public void navigateToGroupsTab() {
        bottomNav.setSelectedItemId(R.id.groupsFragment);
    }
}
