package com.changia.changia_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // 2 seconds
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ensure layout is set immediately to avoid "Launch Timeout"
        setContentView(R.layout.activity_splash);

        // Start background initialization immediately
        executorService.execute(() -> {
            long startTime = System.currentTimeMillis();

            // 1. Initialize Session and Database in background
            SessionManager sessionManager = new SessionManager(SplashActivity.this);
            AppDatabase.getDatabase(SplashActivity.this); // Pre-warm DB connection

            boolean loggedIn = sessionManager.isLoggedIn();

            // 2. Calculate remaining time to ensure splash shows for at least 2s
            long elapsedTime = System.currentTimeMillis() - startTime;
            long remainingTime = Math.max(0, SPLASH_DURATION - elapsedTime);

            // 3. Navigate on the Main Thread
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent;
                if (loggedIn) {
                    Log.d("SPLASH", "User logged in, moving to MainActivity");
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    Log.d("SPLASH", "No session found, moving to LoginActivity");
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }

                startActivity(intent);
                finish();
            }, remainingTime);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // Clean up threads
    }
}