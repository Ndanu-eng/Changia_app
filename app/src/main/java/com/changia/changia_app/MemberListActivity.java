package com.changia.changia_app;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MemberListActivity extends AppCompatActivity {

    private RecyclerView rvMembers;
    private MemberAdapter memberAdapter;
    private TextView tvMemberCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);

        ImageView ivBack = findViewById(R.id.iv_back);
        tvMemberCount = findViewById(R.id.tv_member_count);
        ivBack.setOnClickListener(v -> finish());

        setupRecyclerView();
        loadMembers();
    }

    private void setupRecyclerView() {
        rvMembers = findViewById(R.id.rv_members);
        rvMembers.setLayoutManager(new LinearLayoutManager(this));

        memberAdapter = new MemberAdapter();
        rvMembers.setAdapter(memberAdapter);
    }

    private void loadMembers() {
        List<Member> memberList = new ArrayList<>();
        memberList.add(new Member("Betina Onyango", "+254 712 345 678", true, true)); // Admin, paid
        memberList.add(new Member("Spencer Ngari", "+254 723 456 789", false, true));
        memberList.add(new Member("Lorna Ndanu", "+254 734 567 890", false, false)); // Not paid
        memberList.add(new Member("John Doe", "+254 745 678 901", false, true));
        memberList.add(new Member("Jane Smith", "+254 756 789 012", false, true));

        memberAdapter.setMemberList(memberList);
        tvMemberCount.setText(getString(R.string.member_count, memberList.size()));
    }
}