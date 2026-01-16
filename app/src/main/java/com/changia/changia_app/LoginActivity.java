package com.changia.changia_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etPhoneNumber;
    private EditText etPin;
    private Button btnLogin;
    private TextView tvRegister;
    private TextView tvForgotPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etPin = findViewById(R.id.et_pin);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        tvForgotPin = findViewById(R.id.tv_forgot_pin);
    }

    private void setupListeners() {
        // Phone number formatting (Kenyan format)
        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String phone = s.toString().trim();
                if (!phone.startsWith("+254") && phone.length() > 0) {
                    if (phone.startsWith("07") || phone.startsWith("01")) {
                        phone = "+254" + phone.substring(1);
                        etPhoneNumber.removeTextChangedListener(this);
                        etPhoneNumber.setText(phone);
                        etPhoneNumber.setSelection(phone.length());
                        etPhoneNumber.addTextChangedListener(this);
                    }
                }
            }
        });

        btnLogin.setOnClickListener(v -> attemptLogin());

        tvRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));

        tvForgotPin.setOnClickListener(v -> {
            // Implement forgot PIN flow
            Toast.makeText(this, "Reset PIN functionality coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void attemptLogin() {
        String phone = etPhoneNumber.getText().toString().trim();
        String pin = etPin.getText().toString().trim();

        if (validateInputs(phone, pin)) {
            // For now, simulate login - replace with actual Firebase Auth
            if (pin.equals("1234")) { // Temporary test PIN
                SessionManager session = new SessionManager(this);
                session.createLoginSession("user_001", phone, "Demo User", "demo@changia.com");

                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid PIN", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateInputs(String phone, String pin) {
        if (phone.isEmpty() || phone.length() < 12) {
            etPhoneNumber.setError("Enter a valid Kenyan phone number");
            return false;
        }

        if (pin.isEmpty() || pin.length() != 4) {
            etPin.setError("PIN must be 4 digits");
            return false;
        }

        return true;
    }
}