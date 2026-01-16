package com.changia.changia_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;

public class LockRequestActivity extends AppCompatActivity {

    private TextView tvGroupName, tvCurrentBalance;
    private EditText etReason;
    private Button btnSubmitRequest;
    private TextInputLayout tilReason;
    private boolean isUnlockRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_request);

        initializeViews();
        determineRequestType();
        updateUIForRequestType();
        setupListeners();
        loadGroupInfo();
    }

    private void initializeViews() {
        tvGroupName = findViewById(R.id.tv_group_name);
        tvCurrentBalance = findViewById(R.id.tv_current_balance);
        etReason = findViewById(R.id.et_lock_reason);
        btnSubmitRequest = findViewById(R.id.btn_submit_request);
        tilReason = findViewById(R.id.til_lock_reason);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
    }

    private void determineRequestType() {
        // Check if this is an unlock request
        String requestType = getIntent().getStringExtra("request_type");
        isUnlockRequest = "UNLOCK".equals(requestType);
    }

    private void updateUIForRequestType() {
        if (isUnlockRequest) {
            // Update for UNLOCK
            tvGroupName.setText("Unlock Funds Request");
            btnSubmitRequest.setText("REQUEST UNLOCK");
            tilReason.setHint("Reason for unlocking funds");
            etReason.setHint("e.g., Goal reached, emergency need");
        } else {
            // Default for LOCK
            tvGroupName.setText("Lock Funds Request");
            btnSubmitRequest.setText("REQUEST LOCK");
            tilReason.setHint("Reason for locking funds");
            etReason.setHint("e.g., Saving for venue deposit");
        }
    }

    private void loadGroupInfo() {
        String groupName = getIntent().getStringExtra("group_name");
        if (groupName != null) {
            String prefix = isUnlockRequest ? "Unlock " : "Lock ";
            tvGroupName.setText(prefix + "Funds - " + groupName);
        }

        if (isUnlockRequest) {
            tvCurrentBalance.setText("Locked Balance: KES 540,000");
        } else {
            tvCurrentBalance.setText("Balance: KES 540,000");
        }
    }

    private void setupListeners() {
        btnSubmitRequest.setOnClickListener(v -> {
            if (validateInputs()) {
                if (isUnlockRequest) {
                    submitUnlockRequest();
                } else {
                    submitLockRequest();
                }
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (etReason.getText().toString().trim().isEmpty()) {
            String error = isUnlockRequest ?
                    "Please provide a reason for unlocking funds" :
                    "Please provide a reason for locking funds";
            tilReason.setError(error);
            isValid = false;
        } else {
            tilReason.setError(null);
        }

        return isValid;
    }

    private void submitLockRequest() {
        String reason = etReason.getText().toString().trim();

        // Simulate API call to submit lock request
        Toast.makeText(this, "Lock request submitted to members", Toast.LENGTH_SHORT).show();

        new android.os.Handler().postDelayed(() -> {
            Toast.makeText(this, "Members will vote on this lock request", Toast.LENGTH_SHORT).show();
            finish();
        }, 1500);
    }

    private void submitUnlockRequest() {
        String reason = etReason.getText().toString().trim();

        // Simulate API call to submit unlock request
        Toast.makeText(this, "Unlock request submitted to members", Toast.LENGTH_SHORT).show();

        new android.os.Handler().postDelayed(() -> {
            Toast.makeText(this, "Members will vote on this unlock request", Toast.LENGTH_SHORT).show();
            finish();
        }, 1500);
    }
}