
package com.changia.changia_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private ImageView ivBack;

    private TextInputLayout tilGroupName, tilGroupGoal, tilMemberCount, tilContributionAmount;

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
        ivBack = findViewById(R.id.iv_back);

        tilGroupName = findViewById(R.id.til_group_name);
        tilGroupGoal = findViewById(R.id.til_group_goal);
        tilMemberCount = findViewById(R.id.til_member_count);
        tilContributionAmount = findViewById(R.id.til_contribution_amount);
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());

        btnCreateGroup.setOnClickListener(v -> {
            if (validateInputs()) {
                createGroup();
            }
        });
    }

    /**
     * THIS METHOD IS NOW FIXED.
     * The logic is restructured to prevent the infinite layout loop that causes the app to hang.
     */
    private boolean validateInputs() {
        // Step 1: Clear all previous errors at the beginning.
        // This is the key to breaking the infinite loop.
        tilGroupName.setError(null);
        tilGroupGoal.setError(null);
        tilMemberCount.setError(null);
        tilContributionAmount.setError(null);

        boolean isValid = true;

        // Step 2: Validate each field and set errors if necessary.
        if (etGroupName.getText().toString().trim().isEmpty()) {
            tilGroupName.setError("Group name is required");
            isValid = false;
        }

        if (etGroupGoal.getText().toString().trim().isEmpty()) {
            tilGroupGoal.setError("Group goal is required");
            isValid = false;
        }

        if (etMemberCount.getText().toString().trim().isEmpty()) {
            tilMemberCount.setError("Member count is required");
            isValid = false;
        }

        if (etContributionAmount.getText().toString().trim().isEmpty()) {
            tilContributionAmount.setError("Contribution amount is required");
            isValid = false;
        }

        // Only after all checks are done, return the final result.
        return isValid;
    }

    private void createGroup() {
        String groupName = etGroupName.getText().toString().trim();
        // In a real app, you would parse the other values, but for this simulation,
        // just showing the toast is fine.

        Toast.makeText(this, "Group '" + groupName + "' created successfully!", Toast.LENGTH_LONG).show();

        // Navigate back to the previous screen
        finish();
    }
}
