package com.changia.changia_app;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SendMoneyActivity extends AppCompatActivity {

    private TextView tvAvailableBalance;
    private EditText etAmount;
    private Spinner spinnerGroups;
    private Button btnSendMoney;
    private TextInputLayout tilAmount;

    // --- Database and Session ---
    private AppDatabase appDatabase;
    private SessionManager sessionManager;
    private double currentBalance = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);

        // Initialize database and session
        appDatabase = AppDatabase.getDatabase(getApplicationContext());
        sessionManager = new SessionManager(this);

        initializeViews();
        loadBalance(); // Load from database
        loadGroups();
        setupListeners();
    }

    private void initializeViews() {
        tvAvailableBalance = findViewById(R.id.tv_available_balance);
        etAmount = findViewById(R.id.et_amount);
        spinnerGroups = findViewById(R.id.spinner_groups);
        btnSendMoney = findViewById(R.id.btn_send_money);
        tilAmount = findViewById(R.id.til_amount);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
    }

    /**
     * Load balance from database
     */
    private void loadBalance() {
        String userEmail = sessionManager.getUserEmail();
        if (userEmail == null || userEmail.isEmpty()) return;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            UserEntity user = appDatabase.userDao().getUserByEmail(userEmail);
            if (user != null) {
                currentBalance = user.getWalletBalance();
                runOnUiThread(() -> {
                    tvAvailableBalance.setText(String.format("Wallet Balance: KES %,.2f", currentBalance));
                });
            } else {
                runOnUiThread(() -> {
                    tvAvailableBalance.setText("Wallet Balance: KES 0");
                });
            }
        });
    }

    private void loadGroups() {
        List<String> groups = new ArrayList<>();

        if (sessionManager.isAdmin()) {
            groups.add("Select a group");
            groups.add("💒 Nairobi Wedding Fund 2026");
            groups.add("🚀 Tech Startup Investment");
            groups.add("🏥 Emergency Medical Fund");
            groups.add("📚 Children School Fees 2026");
            groups.add("🏞️ Land Purchase - Kiambu");
            groups.add("✈️ Vacation Fund - Dubai Trip");
            groups.add("🏪 Small Business Capital");
            groups.add("🚗 Car Purchase Fund");
        } else {
            groups.add("No groups available");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                groups
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroups.setAdapter(adapter);
    }

    private void setupListeners() {
        btnSendMoney.setOnClickListener(v -> {
            if (validateInputs()) {
                processSendMoney();
            }
        });
    }

    private boolean validateInputs() {
        // Clear previous errors
        tilAmount.setError(null);
        boolean isValid = true;

        String amountStr = etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            tilAmount.setError("Amount is required");
            isValid = false;
        } else {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount < 100) {
                    tilAmount.setError("Minimum amount is KES 100");
                    isValid = false;
                } else if (amount > currentBalance) {
                    tilAmount.setError("Insufficient wallet balance");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilAmount.setError("Invalid amount");
                isValid = false;
            }
        }

        if (spinnerGroups.getSelectedItemPosition() == 0 ||
                spinnerGroups.getSelectedItem().toString().equals("No groups available")) {
            Toast.makeText(this, "Please select a group", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void processSendMoney() {
        double amountToSend = Double.parseDouble(etAmount.getText().toString().trim());
        String selectedGroup = spinnerGroups.getSelectedItem().toString();

        btnSendMoney.setEnabled(false);
        btnSendMoney.setText("Sending...");

        String userEmail = sessionManager.getUserEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Error: User session not found.", Toast.LENGTH_SHORT).show();
            btnSendMoney.setEnabled(true);
            btnSendMoney.setText("Send Money");
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Background work: Deduct from wallet balance
            UserEntity user = appDatabase.userDao().getUserByEmail(userEmail);
            if (user != null) {
                double newBalance = user.getWalletBalance() - amountToSend;
                user.setWalletBalance(newBalance);
                appDatabase.userDao().updateUser(user);

                runOnUiThread(() -> {
                    Toast.makeText(this,
                            "✅ KES " + String.format("%.2f", amountToSend) + " sent to\n" + selectedGroup + " successfully!",
                            Toast.LENGTH_LONG).show();
                    finish();
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error: Could not process transfer.", Toast.LENGTH_SHORT).show();
                    btnSendMoney.setEnabled(true);
                    btnSendMoney.setText("Send Money");
                });
            }
        });
    }
}