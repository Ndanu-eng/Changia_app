package com.changia.changia_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreateGroupActivity extends AppCompatActivity {

    private EditText etGroupName;
    private EditText etGroupGoal;  // Changed from et_target_amount
    private EditText etMemberCount;
    private EditText etContributionAmount;  // Changed from et_monthly_contribution
    private RadioGroup rgContributionFrequency;
    private RadioButton rbWeekly, rbMonthly;
    private Button btnCreateGroup;  // Changed from btn_create
    private ImageView ivBack;

    private SessionManager sessionManager;
    private GroupRepository groupRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        sessionManager = new SessionManager(this);
        groupRepository = new GroupRepository(getApplication());

        // Initialize views with CORRECT IDs from your layout
        ivBack = findViewById(R.id.iv_back);
        etGroupName = findViewById(R.id.et_group_name);
        etGroupGoal = findViewById(R.id.et_group_goal);  // CORRECT ID
        etMemberCount = findViewById(R.id.et_member_count);  // CORRECT ID
        etContributionAmount = findViewById(R.id.et_contribution_amount);  // CORRECT ID
        rgContributionFrequency = findViewById(R.id.rg_contribution_frequency);
        rbWeekly = findViewById(R.id.rb_weekly);
        rbMonthly = findViewById(R.id.rb_monthly);
        btnCreateGroup = findViewById(R.id.btn_create_group);  // CORRECT ID

        // Set default to monthly
        rbMonthly.setChecked(true);

        // Back button click
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup();
            }
        });
    }

    private void createGroup() {
        String name = etGroupName.getText().toString().trim();
        String goalStr = etGroupGoal.getText().toString().trim();
        String membersStr = etMemberCount.getText().toString().trim();
        String contributionStr = etContributionAmount.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty()) {
            etGroupName.setError("Group name is required");
            etGroupName.requestFocus();
            return;
        }

        if (goalStr.isEmpty()) {
            etGroupGoal.setError("Target amount is required");
            etGroupGoal.requestFocus();
            return;
        }

        if (membersStr.isEmpty()) {
            etMemberCount.setError("Number of members is required");
            etMemberCount.requestFocus();
            return;
        }

        double targetAmount = Double.parseDouble(goalStr);
        int memberCount = Integer.parseInt(membersStr);
        double monthlyContribution = contributionStr.isEmpty() ? 0 : Double.parseDouble(contributionStr);

        // Get selected frequency
        String frequency = rbMonthly.isChecked() ? "Monthly" : "Weekly";

        // Create GroupEntity
        GroupEntity group = new GroupEntity();
        group.setName(name);
        group.setTargetAmount(targetAmount);
        group.setTargetMemberCount(memberCount);
        group.setCurrentMemberCount(1); // Creator is first member
        group.setCurrentAmount(0);
        group.setMonthlyContribution(monthlyContribution);
        group.setContributionFrequency(frequency);
        group.setCreatedBy(sessionManager.getUserId());
        group.setCreatedAt(System.currentTimeMillis());
        group.setLocked(false);

        // Save to database
        groupRepository.insert(group, new GroupRepository.OnGroupInsertedListener() {
            @Override
            public void onGroupInserted(long groupId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CreateGroupActivity.this,
                                "Group created successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }
}