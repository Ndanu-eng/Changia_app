package com.changia.changia_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignUpActivity extends AppCompatActivity {

    // UI Components
    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signUpButton;
    private TextView loginTextView;

    // --- FIX: Replaced DatabaseHelper with Room Database ---
    private AppDatabase appDatabase;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // --- FIX: Initialize Room Database and SessionManager ---
        appDatabase = AppDatabase.getDatabase(getApplicationContext());
        sessionManager = new SessionManager(this);

        // Link the Java objects to the views in your XML layout
        initializeViews();

        // Set up the click listeners
        setupListeners();
    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        loginTextView = findViewById(R.id.loginTextView);
    }

    private void setupListeners() {
        signUpButton.setOnClickListener(v -> handleSignUp());

        loginTextView.setOnClickListener(v -> {
            // Navigate back to LoginActivity
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Main method to handle the sign-up process using Room.
     */
    private void handleSignUp() {
        if (!validateInputs()) {
            // If validation fails, stop the process.
            return;
        }

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // --- FIX: Perform database operations on a background thread ---
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Background work: Check if user already exists
            UserEntity existingUser = appDatabase.userDao().getUserByEmail(email);

            if (existingUser != null) {
                // User already exists, post error message back to the main thread
                runOnUiThread(() -> {
                    Toast.makeText(this, "An account with this email already exists.", Toast.LENGTH_SHORT).show();
                });
            } else {
                // User does not exist, proceed with insertion
                UserEntity newUser = new UserEntity();
                newUser.setEmail(email);
                newUser.setPassword(password); // In a real app, hash the password!
                // Set default values
                newUser.setFullName(extractNameFromEmail(email)); // Set a temporary name
                newUser.setPhoneNumber("");
                newUser.setAdmin(false);
                newUser.setWalletBalance(0);
                newUser.setCreatedAt(System.currentTimeMillis());

                // Insert the new user
                appDatabase.userDao().insertUser(newUser);

                // Post success message and navigate on the main thread
                runOnUiThread(() -> {
                    handleSuccessfulRegistration(newUser);
                });
            }
        });
    }

    /**
     * Handles the flow after a user is successfully added to the database.
     * @param newUser The UserEntity of the newly registered user.
     */
    private void handleSuccessfulRegistration(UserEntity newUser) {
        Toast.makeText(this, "Signup Successful! Please complete your profile.", Toast.LENGTH_SHORT).show();

        // 3. Automatically create a login session for the new user
        sessionManager.createLoginSession(newUser);

        // 4. Navigate to the CompleteProfileActivity
        Intent intent = new Intent(this, CompleteProfileActivity.class);
        startActivity(intent);

        // 5. Clear the back stack so the user cannot navigate back to signup/login
        finishAffinity();
    }

    /**
     * Validates all user inputs from the form.
     * @return true if all inputs are valid, false otherwise.
     */
    private boolean validateInputs() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        boolean isValid = true;

        // --- Email Validation ---
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email address");
            isValid = false;
        } else {
            emailEditText.setError(null);
        }

        // --- Password Validation ---
        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            isValid = false;
        } else {
            passwordEditText.setError(null);
        }

        // --- Confirm Password Validation ---
        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.setError("Please confirm your password");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            isValid = false;
        } else {
            confirmPasswordEditText.setError(null);
        }

        return isValid;
    }

    private String extractNameFromEmail(String email) {
        if (email != null && email.contains("@")) {
            String namePart = email.substring(0, email.indexOf("@"));
            // Capitalize the first letter
            return namePart.substring(0, 1).toUpperCase() + namePart.substring(1);
        }
        return "User";
    }
}
