package com.changia.changia_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompleteProfileActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etPhoneNumber;
    private TextInputLayout tilFullName, tilPhoneNumber;
    private Button btnSaveProfile;

    // --- FIX: Add AppDatabase and keep SessionManager ---
    private AppDatabase appDatabase;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        // --- FIX: Initialize both AppDatabase and SessionManager ---
        appDatabase = AppDatabase.getDatabase(getApplicationContext());
        sessionManager = new SessionManager(this);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        etFullName = findViewById(R.id.et_full_name);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        tilFullName = findViewById(R.id.til_full_name);
        tilPhoneNumber = findViewById(R.id.til_phone_number);
        btnSaveProfile = findViewById(R.id.btn_save_profile);
    }

    private void setupListeners() {
        btnSaveProfile.setOnClickListener(v -> {
            if (validateInputs()) {
                saveProfileAndNavigate();
            }
        });
    }

    /**
     * Validates user inputs with more specific rules.
     * @return true if inputs are valid, false otherwise.
     */
    private boolean validateInputs() {
        // Clear previous errors first
        tilFullName.setError(null);
        tilPhoneNumber.setError(null);

        String name = etFullName.getText().toString().trim();
        String phone = etPhoneNumber.getText().toString().trim();
        boolean isValid = true;

        // Name Validation
        if (name.isEmpty()) {
            tilFullName.setError("Full name is required");
            isValid = false;
        } else if (name.length() < 3) {
            tilFullName.setError("Please enter a valid full name");
            isValid = false;
        }

        // Phone Number Validation (for Kenyan numbers)
        if (phone.isEmpty()) {
            tilPhoneNumber.setError("Phone number is required");
            isValid = false;
        } else if (!phone.matches("^(01|07)\\d{8}$")) {
            tilPhoneNumber.setError("Please enter a valid 10-digit phone number (e.g., 0712345678)");
            isValid = false;
        }

        return isValid;
    }

    /**
     * THIS METHOD IS NOW FIXED.
     * It saves the profile details to BOTH SessionManager and the Room Database.
     */
    private void saveProfileAndNavigate() {
        String name = etFullName.getText().toString().trim();
        String phone = etPhoneNumber.getText().toString().trim();
        String email = sessionManager.getUserEmail(); // Get the current user's email

        // --- FIX: Save to Room Database on a background thread ---
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Background work: Find the user and update their details
            UserEntity user = appDatabase.userDao().getUserByEmail(email);
            if (user != null) {
                user.setFullName(name);
                user.setPhoneNumber(phone);
                appDatabase.userDao().updateUser(user);
            }

            // Post the navigation logic back to the main thread
            runOnUiThread(() -> {
                // Also save to SessionManager for immediate access
                sessionManager.saveProfileDetails(name, phone);

                Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();

                // Navigate to the main dashboard
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        });
    }
}
