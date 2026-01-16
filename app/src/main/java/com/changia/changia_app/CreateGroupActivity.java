package com.changia.changia_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;

public class CreateGroupActivity extends AppCompatActivity {

    private EditText etGroupName, etGroupGoal, etMemberCount, etContributionAmount;
    private RadioGroup rgContributionFrequency;
    private RadioButton rbWeekly, rbMonthly;
    private Button btnCreateGroup;
    private TextInputLayout tilGroupName, tilGroupGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        etGroupName = findViewById(R.id.et_group_name);
        etGroupGoal = findViewById(R.id.et_group_goal);
        etMemberCount = findViewById(R.id.et_member_count);
        etContributionAmount = findViewById(R.id.et_contribution_amount);

        rgContributionFrequency = findViewById(R.id.rg_contribution_frequency);
        rbWeekly = findViewById(R.id.rb_weekly);
        rbMonthly = findViewById(R.id.rb_monthly);

        btnCreateGroup = findViewById(R.id.btn_create_group);

        tilGroupName = findViewById(R.id.til_group_name);
        tilGroupGoal = findViewById(R.id.til_group_goal);
    }

    private void setupListeners() {
        btnCreateGroup.setOnClickListener(v -> {
            if (validateInputs()) {
                createGroup();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (etGroupName.getText().toString().trim().isEmpty()) {
            tilGroupName.setError("Group name is required");
            isValid = false;
        } else {
            tilGroupName.setError(null);
        }

        if (etGroupGoal.getText().toString().trim().isEmpty()) {
            tilGroupGoal.setError("Group goal is required");
            isValid = false;
        } else {
            tilGroupGoal.setError(null);
        }

        return isValid;
    }

    private void createGroup() {
        String groupName = etGroupName.getText().toString().trim();
        String groupGoal = etGroupGoal.getText().toString().trim();
        String memberCount = etMemberCount.getText().toString().trim();
        String contributionAmount = etContributionAmount.getText().toString().trim();

        String frequency = rbWeekly.isChecked() ? "Weekly" : "Monthly";

        // For now, simulate successful group creation
        Toast.makeText(this, "Group '" + groupName + "' created successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}