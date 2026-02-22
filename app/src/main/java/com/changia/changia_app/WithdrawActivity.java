package com.changia.changia_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WithdrawActivity extends AppCompatActivity {

    private TextView tvAvailableBalance;
    private EditText etAmount, etPhoneNumber, etAccountNumber, etBankName;
    private RadioGroup rgWithdrawMethod;
    private RadioButton rbMpesa, rbBank;
    private Button btnWithdraw;
    private TextInputLayout tilAmount, tilPhoneNumber, tilAccountNumber, tilBankName;
    private View layoutMpesa, layoutBank;

    // --- FIX: Add AppDatabase and SessionManager ---
    private AppDatabase appDatabase;
    private SessionManager sessionManager;
    private double currentBalance = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        // --- FIX: Initialize Room Database and SessionManager ---
        appDatabase = AppDatabase.getDatabase(getApplicationContext());
        sessionManager = new SessionManager(this);

        initializeViews();
        loadBalance(); // This will now fetch from the database
        setupListeners();
    }

    private void initializeViews() {
        tvAvailableBalance = findViewById(R.id.tv_available_balance);
        etAmount = findViewById(R.id.et_amount);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etAccountNumber = findViewById(R.id.et_account_number);
        etBankName = findViewById(R.id.et_bank_name);
        rgWithdrawMethod = findViewById(R.id.rg_withdraw_method);
        rbMpesa = findViewById(R.id.rb_mpesa);
        rbBank = findViewById(R.id.rb_bank);
        btnWithdraw = findViewById(R.id.btn_withdraw);
        tilAmount = findViewById(R.id.til_amount);
        tilPhoneNumber = findViewById(R.id.til_phone_number);
        tilAccountNumber = findViewById(R.id.til_account_number);
        tilBankName = findViewById(R.id.til_bank_name);
        layoutMpesa = findViewById(R.id.layout_mpesa);
        layoutBank = findViewById(R.id.layout_bank);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        rbMpesa.setChecked(true);
        showMpesaFields();
    }

    /**
     * --- FIXED METHOD ---
     * Fetches the user's real wallet balance from the Room database.
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
                    tvAvailableBalance.setText(String.format("Available: KES %,.2f", currentBalance));
                });
            }
        });
    }

    private void setupListeners() {
        rgWithdrawMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_mpesa) {
                showMpesaFields();
            } else {
                showBankFields();
            }
        });

        btnWithdraw.setOnClickListener(v -> {
            if (validateInputs()) {
                processWithdrawal();
            }
        });
    }

    private void showMpesaFields() {
        layoutMpesa.setVisibility(View.VISIBLE);
        layoutBank.setVisibility(View.GONE);
    }

    private void showBankFields() {
        layoutMpesa.setVisibility(View.GONE);
        layoutBank.setVisibility(View.VISIBLE);
    }

    /**
     * --- FIXED METHOD ---
     * Correctly validates all inputs and prevents infinite loops.
     */
    private boolean validateInputs() {
        // Clear all previous errors first
        tilAmount.setError(null);
        tilPhoneNumber.setError(null);
        tilAccountNumber.setError(null);
        tilBankName.setError(null);

        boolean isValid = true;

        String amountStr = etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            tilAmount.setError("Amount is required");
            isValid = false;
        } else {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount < 100) {
                    tilAmount.setError("Minimum withdrawal is KES 100");
                    isValid = false;
                } else if (amount > currentBalance) {
                    tilAmount.setError("Withdrawal amount cannot exceed your available balance");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilAmount.setError("Invalid amount");
                isValid = false;
            }
        }

        if (rbMpesa.isChecked()) {
            String phone = etPhoneNumber.getText().toString().trim();
            if (phone.isEmpty()) {
                tilPhoneNumber.setError("Phone number is required");
                isValid = false;
            } else if (!phone.matches("^(01|07)\\d{8}$")) {
                tilPhoneNumber.setError("Invalid phone number format (e.g., 0712345678)");
                isValid = false;
            }
        } else { // Bank fields validation
            if (etAccountNumber.getText().toString().trim().isEmpty()) {
                tilAccountNumber.setError("Account number is required");
                isValid = false;
            }
            if (etBankName.getText().toString().trim().isEmpty()) {
                tilBankName.setError("Bank name is required");
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * --- FIXED METHOD ---
     * Processes the withdrawal by updating the balance in the Room database.
     */
    private void processWithdrawal() {
        double amountToWithdraw = Double.parseDouble(etAmount.getText().toString().trim());

        btnWithdraw.setEnabled(false);
        btnWithdraw.setText("Processing...");

        String userEmail = sessionManager.getUserEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Error: User session not found.", Toast.LENGTH_SHORT).show();
            btnWithdraw.setEnabled(true);
            btnWithdraw.setText("Withdraw");
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Background work: Fetch user, update balance, and save
            UserEntity user = appDatabase.userDao().getUserByEmail(userEmail);
            if (user != null) {
                double newBalance = user.getWalletBalance() - amountToWithdraw;
                user.setWalletBalance(newBalance);
                appDatabase.userDao().updateUser(user);

                runOnUiThread(() -> {
                    Toast.makeText(this, "✅ Withdrawal of KES " + amountToWithdraw + " was successful.", Toast.LENGTH_LONG).show();
                    finish();
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error: Could not process withdrawal.", Toast.LENGTH_SHORT).show();
                    btnWithdraw.setEnabled(true);
                    btnWithdraw.setText("Withdraw");
                });
            }
        });
    }
}
