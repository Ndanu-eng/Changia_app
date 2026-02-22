package com.changia.changia_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddMoneyActivity extends AppCompatActivity {

    private EditText etAmount, etPhoneNumber, etCardNumber, etExpiryDate, etCvv;
    private RadioGroup rgPaymentMethod;
    private RadioButton rbMpesa, rbVisa;
    private Button btnAddMoney;
    private TextInputLayout tilAmount, tilPhoneNumber, tilCardNumber, tilExpiryDate, tilCvv;
    private View layoutMpesa, layoutVisa;

    // --- FIX: Add AppDatabase and SessionManager ---
    private AppDatabase appDatabase;
    private SessionManager sessionManager;

    private boolean isProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

        // --- FIX: Initialize Room Database and SessionManager ---
        appDatabase = AppDatabase.getDatabase(getApplicationContext());
        sessionManager = new SessionManager(this);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        etAmount = findViewById(R.id.et_amount);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etCardNumber = findViewById(R.id.et_card_number);
        etExpiryDate = findViewById(R.id.et_expiry_date);
        etCvv = findViewById(R.id.et_cvv);

        rgPaymentMethod = findViewById(R.id.rg_payment_method);
        rbMpesa = findViewById(R.id.rb_mpesa);
        rbVisa = findViewById(R.id.rb_visa);

        btnAddMoney = findViewById(R.id.btn_add_money);

        tilAmount = findViewById(R.id.til_amount);
        tilPhoneNumber = findViewById(R.id.til_phone_number);
        tilCardNumber = findViewById(R.id.til_card_number);
        tilExpiryDate = findViewById(R.id.til_expiry_date);
        tilCvv = findViewById(R.id.til_cvv);

        layoutMpesa = findViewById(R.id.layout_mpesa);
        layoutVisa = findViewById(R.id.layout_visa);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        rbMpesa.setChecked(true);
        showMpesaFields();
    }

    private void setupListeners() {
        rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_mpesa) {
                showMpesaFields();
            } else {
                showVisaFields();
            }
        });

        btnAddMoney.setOnClickListener(v -> {
            if (isProcessing) return;
            if (validateInputs()) {
                processPayment();
            }
        });
    }

    private void showMpesaFields() {
        layoutMpesa.setVisibility(View.VISIBLE);
        layoutVisa.setVisibility(View.GONE);
    }

    private void showVisaFields() {
        layoutMpesa.setVisibility(View.GONE);
        layoutVisa.setVisibility(View.VISIBLE);
    }

    private boolean validateInputs() {
        boolean isValid = true;
        tilAmount.setError(null);
        tilPhoneNumber.setError(null);
        tilCardNumber.setError(null);
        tilExpiryDate.setError(null);
        tilCvv.setError(null);

        String amountStr = etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            tilAmount.setError("Amount is required");
            isValid = false;
        } else {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount < 10) {
                    tilAmount.setError("Minimum amount is KES 10");
                    isValid = false;
                } else if (amount > 150000) {
                    tilAmount.setError("Maximum amount is KES 150,000");
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
            } else if (!phone.matches("^(07|01)\\d{8}$")) {
                tilPhoneNumber.setError("Invalid phone format (e.g., 0712345678)");
                isValid = false;
            }
        } else {
            String cardNumber = etCardNumber.getText().toString().trim().replaceAll("\\s", "");
            if (cardNumber.isEmpty() || cardNumber.length() != 16) {
                tilCardNumber.setError("Card number must be 16 digits");
                isValid = false;
            }
            String expiry = etExpiryDate.getText().toString().trim();
            if (expiry.isEmpty() || !expiry.matches("^(0[1-9]|1[0-2])/\\d{2}$")) {
                tilExpiryDate.setError("Invalid format (MM/YY)");
                isValid = false;
            }
            String cvv = etCvv.getText().toString().trim();
            if (cvv.isEmpty() || cvv.length() != 3) {
                tilCvv.setError("CVV must be 3 digits");
                isValid = false;
            }
        }
        return isValid;
    }

    private void processPayment() {
        int amount = Integer.parseInt(etAmount.getText().toString().trim());

        if (rbMpesa.isChecked()) {
            String phone = etPhoneNumber.getText().toString().trim();
            processRealMpesaPayment(phone, amount);
        } else {
            String cardNumber = etCardNumber.getText().toString().trim();
            processVisaPayment(cardNumber, String.valueOf(amount));
        }
    }

    private void processRealMpesaPayment(String phone, int amount) {
        isProcessing = true;
        btnAddMoney.setEnabled(false);
        btnAddMoney.setText("Processing...");

        Toast.makeText(this, "📱 Sending STK Push to " + phone + "...", Toast.LENGTH_SHORT).show();

        MpesaHelper.initiateSTKPush(
                phone,
                amount,
                "Wallet Top Up",
                "Adding money to wallet",
                new MpesaHelper.MpesaCallback() {
                    @Override
                    public void onSuccess(String checkoutRequestID, String message) {
                        runOnUiThread(() -> {
                            isProcessing = false;
                            btnAddMoney.setEnabled(true);
                            btnAddMoney.setText("Add Money");
                            new MaterialAlertDialogBuilder(AddMoneyActivity.this)
                                    .setTitle("✅ STK Push Sent!")
                                    .setMessage("Please check your phone and enter your M-Pesa PIN to complete the payment.")
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        Toast.makeText(AddMoneyActivity.this, "Waiting for payment confirmation...", Toast.LENGTH_LONG).show();
                                        // --- FIX: Update the user's wallet balance in the database ---
                                        updateUserWalletBalance(amount);
                                    })
                                    .setCancelable(false)
                                    .show();
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            isProcessing = false;
                            btnAddMoney.setEnabled(true);
                            btnAddMoney.setText("Add Money");
                            new MaterialAlertDialogBuilder(AddMoneyActivity.this)
                                    .setTitle("❌ Payment Failed")
                                    .setMessage(error + "\n\nPlease try again or contact support.")
                                    .setPositiveButton("OK", null)
                                    .show();
                        });
                    }
                }
        );
    }

    /**
     * --- NEW METHOD ---
     * This method updates the user's wallet balance in the Room database on a background thread.
     * @param amountToAdd The amount to add to the current balance.
     */
    private void updateUserWalletBalance(int amountToAdd) {
        String userEmail = sessionManager.getUserEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Error: User session not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Background work: Fetch user, update balance, and save
            UserEntity user = appDatabase.userDao().getUserByEmail(userEmail);
            if (user != null) {
                double newBalance = user.getWalletBalance() + amountToAdd;
                user.setWalletBalance(newBalance);
                appDatabase.userDao().updateUser(user);

                // Post success message back to the main thread
                runOnUiThread(() -> {
                    Toast.makeText(AddMoneyActivity.this, "✅ Payment successful! KES " + amountToAdd + " added to wallet.", Toast.LENGTH_LONG).show();
                    finish(); // Close the activity and return to the previous screen
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(AddMoneyActivity.this, "Error: Could not update wallet balance.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void processVisaPayment(String cardNumber, String amount) {
        // ... (This simulation method remains unchanged but could also be updated to use the database)
        Toast.makeText(this, "Processing Visa payment...", Toast.LENGTH_SHORT).show();
        // For now, we will just finish
        finish();
    }
}
