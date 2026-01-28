package com.changia.changia_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

public class ProfileActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private AppDatabase appDatabase;

    private ImageView ivBack;
    private TextView tvUserName, tvUserPhone, tvUserEmail, tvAccountType, tvMemberSince;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);
        appDatabase = AppDatabase.getDatabase(this);

        if (!sessionManager.isLoggedIn()) {
            sessionManager.logoutUser();
            return;
        }

        initializeViews();
        observeUserData();
        setupListeners();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserPhone = findViewById(R.id.tv_user_phone);
        tvUserEmail = findViewById(R.id.tv_user_email);
        tvAccountType = findViewById(R.id.tv_account_type);
        tvMemberSince = findViewById(R.id.tv_member_since);
        btnLogout = findViewById(R.id.btn_logout);

        // 🎨 Show if admin
        if (sessionManager.isAdmin()) {
            tvAccountType.setText("👑 Admin Account");
            tvAccountType.setTextColor(getColor(R.color.primary_color));
        } else {
            tvAccountType.setText("👤 Regular Member");
            tvAccountType.setTextColor(getColor(R.color.text_secondary));
        }
    }

    private void observeUserData() {
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            tvUserName.setText("Error");
            tvUserPhone.setText("Could not load user data");
            tvUserEmail.setText("");
            return;
        }

        LiveData<UserEntity> userLiveData = appDatabase.userDao().getUserByIdLive(userId);

        userLiveData.observe(this, userEntity -> {
            if (userEntity != null) {
                String name = userEntity.getFullName();
                String phone = userEntity.getPhoneNumber();
                String email = userEntity.getEmail();

                tvUserName.setText(name != null && !name.isEmpty() ? name : "User");
                tvUserPhone.setText(phone != null && !phone.isEmpty() ? phone : "No phone number");
                tvUserEmail.setText(email);

                // 🎨 Show member since date
                long createdAt = userEntity.getCreatedAt();
                String memberSince = new java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault())
                        .format(new java.util.Date(createdAt));
                tvMemberSince.setText("Member since: " + memberSince);
            }
        });
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());
        btnLogout.setOnClickListener(v -> sessionManager.logoutUser());
    }
}