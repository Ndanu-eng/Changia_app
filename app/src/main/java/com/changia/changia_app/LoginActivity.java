package com.changia.changia_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // UI Components
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView signUpTextView;

    private AppDatabase appDatabase;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        appDatabase = AppDatabase.getDatabase(getApplicationContext());
        sessionManager = new SessionManager(this);

        // Check if the user is already logged in with a valid session
        if (sessionManager.isLoggedIn() && sessionManager.getUserId() != -1) {
            Log.d(TAG, "User is already logged in with a valid session. Navigating to MainActivity.");
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return; // Stop further execution of onCreate
        } else {
            // If the session is invalid (e.g., logged in but no ID), clear it for safety.
            if (sessionManager.isLoggedIn()) {
                Log.w(TAG, "User was logged in but session data is invalid. Clearing session.");
                // Note: logoutUser() in this context will clear SharedPreferences but not navigate yet,
                // which is correct behavior here.
                sessionManager.logoutUser();
            }
        }

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpTextView = findViewById(R.id.signUpTextView);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> validateAndLogin());

        signUpTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void validateAndLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email address");
            emailEditText.requestFocus();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            UserEntity user = appDatabase.userDao().login(email, password);
            runOnUiThread(() -> {
                if (user != null) {
                    handleSuccessfulLogin(user);
                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void handleSuccessfulLogin(UserEntity user) {
        // --- THIS IS THE FIX ---
        // The createLoginSession method now handles EVERYTHING, including the admin status.
        // By calling only this method, we ensure all data is saved in one atomic operation.
        sessionManager.createLoginSession(user);

        // REMOVED: sessionManager.setAdmin(user.isAdmin()); // This line was causing the bug.

        if (user.isAdmin()) {
            Toast.makeText(this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
