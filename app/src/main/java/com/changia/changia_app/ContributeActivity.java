package com.changia.changia_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;

public class ContributeActivity extends AppCompatActivity {

    private TextView tvGroupName, tvCurrentBalance, tvNextPayout;
    private EditText etAmount;
    private RadioGroup rgPaymentMethod;
    private RadioButton rbMpesa, rbVisa;
    private Button btnContribute;
    private TextInputLayout tilAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute);

        initializeViews();
        setupListeners();
        loadGroupInfo();
    }

    private void initializeViews() {
        tvGroupName = findViewById(R.id.tv_group_name);
        tvCurrentBalance = findViewById(R.id.tv_current_balance);
        tvNextPayout = findViewById(R.id.tv_next_payout);
        etAmount = findViewById(R.id.et_amount);
        rgPaymentMethod = findViewById(R.id.rg_payment_method);
        rbMpesa = findViewById(R.id.rb_mpesa);
        rbVisa = findViewById(R.id.rb_visa);
        btnContribute = findViewById(R.id.btn_contribute);
        tilAmount = findViewById(R.id.til_amount);

        // Set back button
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
    }

    private void loadGroupInfo() {
        // Get from intent or API
        String groupName = getIntent().getStringExtra("group_name");
        if (groupName != null) {
            tvGroupName.setText(groupName);
        }

        tvCurrentBalance.setText("Current Balance: KES 540,000");
        tvNextPayout.setText("Next Payout: Oct 15, 2024");

        // Set default amount (monthly contribution)
        etAmount.setText("5000");
    }

    private void setupListeners() {
        btnContribute.setOnClickListener(v -> {
            if (validateInputs()) {
                processContribution();
            }
        });
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
                if (amount <= 0) {
                    tilAmount.setError("Amount must be greater than 0");
                    isValid = false;
                } else {
                    tilAmount.setError(null);
                }
            } catch (NumberFormatException e) {
                tilAmount.setError("Invalid amount");
                isValid = false;
            }
        }

        if (rgPaymentMethod.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void processContribution() {
        String amount = etAmount.getText().toString().trim();
        String paymentMethod = rbMpesa.isChecked() ? "M-Pesa" : "Visa";

        // Simulate payment processing
        Toast.makeText(this, "Processing " + paymentMethod + " payment of KES " + amount, Toast.LENGTH_SHORT).show();

        // Simulate successful payment
        new android.os.Handler().postDelayed(() -> {
            Toast.makeText(this, "Payment successful! KES " + amount + " contributed.", Toast.LENGTH_SHORT).show();
            finish();
        }, 2000);
    }
}