package com.changia.changia_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class GroupDetailsActivity extends AppCompatActivity {

    private TextView tvGroupName, tvGroupBalance, tvProgress, tvMemberCount;
    private ProgressBar progressBar;
    private Button btnContribute, btnLockFunds, btnViewMembers;
    private ImageView ivBack;
    private RecyclerView rvRecentActivity;
    private ActivityAdapter activityAdapter;
    private boolean isLocked = false;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        sessionManager = new SessionManager(this);

        initializeViews();
        loadGroupData();
        setupListeners();
        setupRecentActivity();
    }

    private void initializeViews() {
        tvGroupName = findViewById(R.id.tv_group_name);
        tvGroupBalance = findViewById(R.id.tv_group_balance);
        tvProgress = findViewById(R.id.tv_progress);
        tvMemberCount = findViewById(R.id.tv_member_count);
        progressBar = findViewById(R.id.progress_bar);
        btnContribute = findViewById(R.id.btn_contribute);
        btnLockFunds = findViewById(R.id.btn_lock_funds);
        btnViewMembers = findViewById(R.id.btn_view_members);
        ivBack = findViewById(R.id.iv_back);
        rvRecentActivity = findViewById(R.id.rv_recent_activity);
    }

    private void loadGroupData() {
        // Get group data from intent
        String groupName = getIntent().getStringExtra("group_name");
        double groupBalance = getIntent().getDoubleExtra("group_balance", 540000);
        int progress = getIntent().getIntExtra("group_progress", 75);

        if (groupName != null) {
            tvGroupName.setText(groupName);
            // Example logic: Wedding Fund is locked
            isLocked = groupName.contains("Wedding") || groupName.contains("School");
        }

        tvGroupBalance.setText(String.format("KES %,.0f", groupBalance));

        if (sessionManager.isAdmin()) {
            tvMemberCount.setText("12 members");
        } else {
            tvMemberCount.setText("1 member"); // Just the user
        }

        // Set progress
        progressBar.setProgress(progress);
        tvProgress.setText(progress + "% of goal reached");

        // Update lock button text based on status
        updateLockButton();
    }

    private void updateLockButton() {
        if (isLocked) {
            btnLockFunds.setText("🔓 Unlock Funds");
            btnLockFunds.setBackgroundTintList(getResources().getColorStateList(R.color.success));
        } else {
            btnLockFunds.setText("🔒 Lock Funds");
            btnLockFunds.setBackgroundTintList(getResources().getColorStateList(R.color.warning));
        }
    }

    private void setupRecentActivity() {
        List<ActivityItem> activityList = new ArrayList<>();

        if (sessionManager.isAdmin()) {
            // Admin sees mock activity
            activityList.add(new ActivityItem("Spencer contributed KES 5,000", "2 hours ago"));
            activityList.add(new ActivityItem("Betina locked funds", "1 day ago"));
            activityList.add(new ActivityItem("Lorna joined the group", "3 days ago"));
            activityList.add(new ActivityItem("Monthly payout to John", "1 week ago"));
        } else {
            // Regular users see no activity or just their own
            activityList.add(new ActivityItem("You created this group", "Just now"));
        }

        activityAdapter = new ActivityAdapter(activityList);
        rvRecentActivity.setLayoutManager(new LinearLayoutManager(this));
        rvRecentActivity.setAdapter(activityAdapter);
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());

        btnContribute.setOnClickListener(v -> {
            Intent intent = new Intent(this, ContributeActivity.class);
            intent.putExtra("group_name", tvGroupName.getText().toString());
            startActivity(intent);
        });

        btnLockFunds.setOnClickListener(v -> {
            Intent intent = new Intent(this, LockRequestActivity.class);
            intent.putExtra("group_name", tvGroupName.getText().toString());

            // Pass request type: LOCK or UNLOCK
            if (isLocked) {
                intent.putExtra("request_type", "UNLOCK");
            } else {
                intent.putExtra("request_type", "LOCK");
            }

            startActivity(intent);

            // In real app, this status would come from API response
            // For demo, toggle the status when user clicks
            isLocked = !isLocked;
            updateLockButton();
        });

        btnViewMembers.setOnClickListener(v -> {
            Intent intent = new Intent(this, MemberListActivity.class);
            intent.putExtra("group_name", tvGroupName.getText().toString());
            startActivity(intent);
        });
    }
}