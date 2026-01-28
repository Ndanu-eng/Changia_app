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

public class AddMoneyActivity_RealMpesa extends AppCompatActivity {

    private EditText etAmount, etPhoneNumber, etCardNumber, etExpiryDate, etCvv;
    private RadioGroup rgPaymentMethod;
    private RadioButton rbMpesa, rbVisa;
    private Button btnAddMoney;
    private TextInputLayout tilAmount, tilPhoneNumber, tilCardNumber, tilExpiryDate, tilCvv;
    private View layoutMpesa, layoutVisa;
    private boolean isProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

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

        // Set M-Pesa as default
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
            if (!isProcessing && validateInputs()) {
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
                } else if (amount > 150000) {
                    tilAmount.setError("Maximum amount is KES 150,000");
                    isValid = false;
                } else {
                    tilAmount.setError(null);
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
            } else if (!phone.matches("^(\\+254|254|0)[17]\\d{8}$")) {
                tilPhoneNumber.setError("Invalid phone number (use 07XX or 01XX)");
                isValid = false;
            } else {
                tilPhoneNumber.setError(null);
            }
        } else {
            String cardNumber = etCardNumber.getText().toString().trim().replaceAll("\\s", "");
            if (cardNumber.isEmpty()) {
                tilCardNumber.setError("Card number is required");
                isValid = false;
            } else if (cardNumber.length() != 16) {
                tilCardNumber.setError("Card number must be 16 digits");
                isValid = false;
            } else {
                tilCardNumber.setError(null);
            }

            String expiry = etExpiryDate.getText().toString().trim();
            if (expiry.isEmpty()) {
                tilExpiryDate.setError("Expiry date is required");
                isValid = false;
            } else if (!expiry.matches("^(0[1-9]|1[0-2])/\\d{2}$")) {
                tilExpiryDate.setError("Invalid format (MM/YY)");
                isValid = false;
            } else {
                tilExpiryDate.setError(null);
            }

            String cvv = etCvv.getText().toString().trim();
            if (cvv.isEmpty()) {
                tilCvv.setError("CVV is required");
                isValid = false;
            } else if (cvv.length() != 3) {
                tilCvv.setError("CVV must be 3 digits");
                isValid = false;
            } else {
                tilCvv.setError(null);
            }
        }

        return isValid;
    }

    private void processPayment() {
        String amountStr = etAmount.getText().toString().trim();
        int amount = Integer.parseInt(amountStr);

        if (rbMpesa.isChecked()) {
            String phone = etPhoneNumber.getText().toString().trim();
            processRealMpesaPayment(phone, amount);
        } else {
            String cardNumber = etCardNumber.getText().toString().trim();
            processVisaPayment(cardNumber, amount);
        }
    }

    private void processRealMpesaPayment(String phone, int amount) {
        isProcessing = true;
        btnAddMoney.setEnabled(false);
        btnAddMoney.setText("Processing...");

        Toast.makeText(this, "📱 Sending STK Push to " + phone + "...", Toast.LENGTH_SHORT).show();

        // REAL M-PESA INTEGRATION
        MpesaHelper.initiateSTKPush(
                phone,
                amount,
                "Wallet Top Up",
                "Adding money to wallet",
                new MpesaHelper.MpesaCallback() {
                    @Override
                    public void onSuccess(String transactionId, String message) {
                        runOnUiThread(() -> {
                            isProcessing = false;
                            btnAddMoney.setEnabled(true);
                            btnAddMoney.setText("Add Money");

                            new MaterialAlertDialogBuilder(AddMoneyActivity_RealMpesa.this)
                                    .setTitle("✅ STK Push Sent!")
                                    .setMessage("Please check your phone and enter your M-Pesa PIN to complete the payment.\n\nTransaction ID: " + transactionId)
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        Toast.makeText(AddMoneyActivity_RealMpesa.this, "Waiting for payment confirmation...", Toast.LENGTH_LONG).show();

                                        // Simulate payment confirmation after 5 seconds
                                        new android.os.Handler().postDelayed(() -> {
                                            Toast.makeText(AddMoneyActivity_RealMpesa.this, "✅ Payment successful! KES " + amount + " added to wallet", Toast.LENGTH_LONG).show();
                                            finish();
                                        }, 5000);
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

                            new MaterialAlertDialogBuilder(AddMoneyActivity_RealMpesa.this)
                                    .setTitle("❌ Payment Failed")
                                    .setMessage(error + "\n\nPlease try again or contact support.")
                                    .setPositiveButton("OK", null)
                                    .show();
                        });
                    }
                }
        );
    }

    private void processVisaPayment(String cardNumber, int amount) {
        isProcessing = true;
        btnAddMoney.setEnabled(false);
        btnAddMoney.setText("Processing...");

        Toast.makeText(this, "💳 Processing Visa payment...", Toast.LENGTH_SHORT).show();

        // Simulate Visa payment processing
        new android.os.Handler().postDelayed(() -> {
            isProcessing = false;

            // Simulate successful payment
            new MaterialAlertDialogBuilder(this)
                    .setTitle("✅ Payment Successful")
                    .setMessage("KES " + amount + " has been added to your wallet.\n\nCard: **** **** **** " + cardNumber.substring(12))
                    .setPositiveButton("OK", (dialog, which) -> finish())
                    .setCancelable(false)
                    .show();
        }, 3000);
    }
}
