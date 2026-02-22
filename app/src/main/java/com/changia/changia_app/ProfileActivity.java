package com.changia.changia_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvUserName;
    private TextView tvUserPhone;
    private TextView tvUserEmail;
    private TextView tvAccountType;
    private TextView tvMemberSince;
    private Button btnLogout;

    private SessionManager sessionManager;
    private AppDatabase appDatabase;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);
        appDatabase = AppDatabase.getDatabase(this);
        userId = sessionManager.getUserId();

        // Initialize views with CORRECT IDs from your layout
        ivBack = findViewById(R.id.iv_back);
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserPhone = findViewById(R.id.tv_user_phone);
        tvUserEmail = findViewById(R.id.tv_user_email);
        tvAccountType = findViewById(R.id.tv_account_type);
        tvMemberSince = findViewById(R.id.tv_member_since);
        btnLogout = findViewById(R.id.btn_logout);

        // Back button click
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Logout button click
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        loadUserData();
    }

    private void loadUserData() {
        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        LiveData<UserEntity> userLiveData = appDatabase.userDao().getUserByIdLive(userId);
        userLiveData.observe(this, new Observer<UserEntity>() {
            @Override
            public void onChanged(UserEntity user) {
                if (user != null) {
                    tvUserName.setText(user.getFullName());
                    tvUserEmail.setText(user.getEmail());

                    String phone = user.getPhoneNumber();
                    tvUserPhone.setText(phone != null && !phone.isEmpty() ? phone : "Phone not set");

                    // Set account type
                    if (user.isAdmin()) {
                        tvAccountType.setText("👑 Admin Account");
                    } else {
                        tvAccountType.setText("👤 Regular Member");
                    }

                    // Format member since date
                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                    String dateStr = sdf.format(new Date(user.getCreatedAt()));
                    tvMemberSince.setText("Member since: " + dateStr);
                }
            }
        });
    }

    private void logout() {
        // Clear session
        sessionManager.logoutUser();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Navigate to Login and clear back stack
        finish();
    }
}