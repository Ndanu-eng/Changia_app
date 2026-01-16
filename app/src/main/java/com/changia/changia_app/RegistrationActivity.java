package com.changia.changia_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etPhoneNumber, etName, etPin;
    private Button btnRegister;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etName = findViewById(R.id.et_name);
        etPin = findViewById(R.id.et_pin);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> {
            // Registration logic here
            String phone = etPhoneNumber.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String pin = etPin.getText().toString().trim();

            if (validateInputs(phone, name, pin)) {
                // For now, just simulate registration
                SessionManager session = new SessionManager(this);
                session.createLoginSession("user_001", phone, name, "demo@changia.com");

                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private boolean validateInputs(String phone, String name, String pin) {
        return !phone.isEmpty() && !name.isEmpty() && pin.length() == 4;
    }
}