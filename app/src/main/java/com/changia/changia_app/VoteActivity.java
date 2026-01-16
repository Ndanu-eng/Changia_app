package com.changia.changia_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class VoteActivity extends AppCompatActivity {

    private TextView tvRequestTitle, tvRequestDetails, tvVoteStatus;
    private Button btnApprove, btnReject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        initializeViews();
        loadVoteRequest();
        setupListeners();
    }

    private void initializeViews() {
        tvRequestTitle = findViewById(R.id.tv_request_title);
        tvRequestDetails = findViewById(R.id.tv_request_details);
        tvVoteStatus = findViewById(R.id.tv_vote_status);
        btnApprove = findViewById(R.id.btn_approve);
        btnReject = findViewById(R.id.btn_reject);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
    }

    private void loadVoteRequest() {
        // Get from intent or API
        String requestType = getIntent().getStringExtra("request_type");
        String groupName = getIntent().getStringExtra("group_name");

        if (requestType == null) requestType = "Lock Funds";
        if (groupName == null) groupName = "Wedding Fund";

        tvRequestTitle.setText(requestType + " Request");
        tvRequestDetails.setText("Betina requested to " + requestType.toLowerCase() +
                " KES 540,000 for " + groupName +
                "\n\nReason: \"Saving for venue deposit\"");
        tvVoteStatus.setText("3/12 members have voted");
    }

    private void setupListeners() {
        btnApprove.setOnClickListener(v -> {
            submitVote(true);
        });

        btnReject.setOnClickListener(v -> {
            submitVote(false);
        });
    }

    private void submitVote(boolean isApprove) {
        String vote = isApprove ? "approve" : "reject";

        Toast.makeText(this, "You voted to " + vote + " this request", Toast.LENGTH_SHORT).show();

        // Simulate API call
        new android.os.Handler().postDelayed(() -> {
            Toast.makeText(this, "Vote recorded. Waiting for other members.", Toast.LENGTH_SHORT).show();
            finish();
        }, 1500);
    }
}