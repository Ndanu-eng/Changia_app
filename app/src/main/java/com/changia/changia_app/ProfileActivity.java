package com.changia.changia_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserPhone, tvUserEmail;
    private CardView cvPersonalInfo, cvSecurity, cvNotifications, cvHelp;
    private Button btnLogout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);

        initializeViews();
        loadUserData();
        setupListeners();
    }

    private void initializeViews() {
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserPhone = findViewById(R.id.tv_user_phone);
        tvUserEmail = findViewById(R.id.tv_user_email);

        cvPersonalInfo = findViewById(R.id.cv_personal_info);
        cvSecurity = findViewById(R.id.cv_security);
        cvNotifications = findViewById(R.id.cv_notifications);
        cvHelp = findViewById(R.id.cv_help);

        btnLogout = findViewById(R.id.btn_logout);
    }

    private void loadUserData() {
        tvUserName.setText(sessionManager.getUserName());
        tvUserPhone.setText(sessionManager.getUserPhone());
        tvUserEmail.setText(sessionManager.getUserEmail());
    }

    private void setupListeners() {
        cvPersonalInfo.setOnClickListener(v -> showComingSoonDialog("Personal Information"));

        cvSecurity.setOnClickListener(v -> {
            showComingSoonDialog("Security & PIN Settings");
        });

        cvNotifications.setOnClickListener(v -> {
            showComingSoonDialog("Notification Preferences");
        });

        cvHelp.setOnClickListener(v -> {
            showComingSoonDialog("Help & Support");
        });

        btnLogout.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        sessionManager.logout();
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void showComingSoonDialog(String feature) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(feature)
                .setMessage("This feature is coming soon!")
                .setPositiveButton("OK", null)
                .show();
    }
}