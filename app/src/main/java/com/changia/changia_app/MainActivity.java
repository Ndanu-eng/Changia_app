package com.changia.changia_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private AppBarConfiguration appBarConfiguration;
    private SessionManager sessionManager;
    private NavigationView navigationView;
    private BottomNavigationView bottomNav;
    private AppDatabase appDatabase;
    private TextView tvUserName, tvUserPhone;
    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("DEBUG", "=== MAIN ACTIVITY STARTED ===");

        // Initialize session manager and database
        sessionManager = new SessionManager(this);
        appDatabase = AppDatabase.getDatabase(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            // Redirect to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setupToolbar();
        setupNavigation();
        setupDrawerHeader();

        // Load user data for drawer header
        loadUserData();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Changia");
        }

        Log.d("DEBUG", "Toolbar setup complete");
    }

    private void setupNavigation() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomNav = findViewById(R.id.bottom_nav);

        Log.d("DEBUG", "Starting navigation setup");

        // Get NavController from NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment == null) {
            Log.e("DEBUG", "ERROR: navHostFragment is NULL!");
            return;
        }

        NavController navController = navHostFragment.getNavController();
        Log.d("DEBUG", "NavController obtained successfully");

        // Configure top level destinations (matches your bottom nav items)
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment,
                R.id.groupsFragment,
                R.id.walletFragment,
                R.id.chamaFragment)
                .setOpenableLayout(drawerLayout)
                .build();

        // Setup ActionBar with NavController
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Setup Bottom Navigation
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Setup Navigation Drawer - THIS LINKS THE DRAWER MENU TO NAVIGATION
        NavigationUI.setupWithNavController(navigationView, navController);

        // Handle custom drawer items (profile, logout) that aren't in navigation graph
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                // Handle custom items
                if (id == R.id.nav_profile) {
                    // Navigate to Profile Activity
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawers();
                    return true;

                } else if (id == R.id.nav_logout) {
                    // Show logout confirmation
                    showLogoutConfirmation();
                    drawerLayout.closeDrawers();
                    return true;
                }

                // For fragment destinations (homeFragment, groupsFragment, etc.),
                // let NavigationUI handle them
                boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
                if (handled) {
                    drawerLayout.closeDrawers();
                }
                return handled;
            }
        });

        Log.d("DEBUG", "=== NAVIGATION SETUP COMPLETE ===");
    }

    private void setupDrawerHeader() {
        // Get header view
        View headerView = navigationView.getHeaderView(0);

        tvUserName = headerView.findViewById(R.id.tv_user_name);
        tvUserPhone = headerView.findViewById(R.id.tv_user_phone);

        // Set default values while loading
        tvUserName.setText("Loading...");
        tvUserPhone.setText("Please wait");
    }

    private void loadUserData() {
        int userId = sessionManager.getUserId();

        if (userId == -1) {
            // Invalid user ID, logout
            sessionManager.logoutUser();
            return;
        }

        // Observe user data from database
        LiveData<UserEntity> userLiveData = appDatabase.userDao().getUserByIdLive(userId);
        userLiveData.observe(this, new Observer<UserEntity>() {
            @Override
            public void onChanged(UserEntity user) {
                if (user != null) {
                    // Update drawer header with real user data
                    tvUserName.setText(user.getFullName());

                    String phone = user.getPhoneNumber();
                    if (phone != null && !phone.isEmpty()) {
                        tvUserPhone.setText(phone);
                    } else {
                        tvUserPhone.setText(user.getEmail());
                    }

                    Log.d("DEBUG", "Drawer header updated for user: " + user.getFullName());
                }
            }
        });
    }

    private void showLogoutConfirmation() {
        Snackbar.make(drawerLayout, "Logout from Changia?", Snackbar.LENGTH_LONG)
                .setAction("LOGOUT", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logout();
                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_dark))
                .show();
    }

    private void logout() {
        // Clear session
        sessionManager.logoutUser();

        // Show message
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Navigate to Login
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        // Close drawer if open
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
            return;
        }

        // Double back to exit
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            if (backToast != null) {
                backToast.cancel();
            }
            super.onBackPressed();
            finishAffinity(); // Close all activities
            return;
        } else {
            backToast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    /**
     * Public method to navigate to groups tab from other fragments
     */
    public void navigateToGroups() {
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.groupsFragment);
        }
    }

    /**
     * Public method to navigate to wallet tab
     */
    public void navigateToWallet() {
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.walletFragment);
        }
    }

    /**
     * Get current user ID
     */
    public int getCurrentUserId() {
        return sessionManager.getUserId();
    }

    /**
     * Check if current user is admin
     */
    public boolean isCurrentUserAdmin() {
        return sessionManager.isAdmin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh user data when returning to activity
        loadUserData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel any pending toasts
        if (backToast != null) {
            backToast.cancel();
        }
    }
}