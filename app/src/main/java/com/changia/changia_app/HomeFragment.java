package com.changia.changia_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvBalance, tvUserName, tvUpcomingPayout;
    private RecyclerView rvGroups;
    private Button btnNewGroup, btnViewAll, btnLockFunds;
    private GroupAdapter groupAdapter;
    private List<Group> groupList;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews(view);
        setupRecyclerView();
        loadData();
        setupListeners();

        return view;
    }

    private void initializeViews(View view) {
        tvBalance = view.findViewById(R.id.tv_balance);
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUpcomingPayout = view.findViewById(R.id.tv_upcoming_payout);
        rvGroups = view.findViewById(R.id.rv_groups);
        btnNewGroup = view.findViewById(R.id.btn_new_group);
        btnViewAll = view.findViewById(R.id.btn_view_all);
        btnLockFunds = view.findViewById(R.id.btn_lock_funds);

        // Set user name from session
        SessionManager session = new SessionManager(requireContext());
        tvUserName.setText("Hi, " + session.getUserName());
    }

    private void setupRecyclerView() {
        groupList = new ArrayList<>();
        groupAdapter = new GroupAdapter(groupList, requireContext());

        rvGroups.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvGroups.setAdapter(groupAdapter);
    }

    private void loadData() {
        // Sample data - replace with actual API call
        groupList.clear();
        groupList.add(new Group(
                "Wedding Fund",
                6,
                10,
                540000,
                750000,
                true
        ));
        groupList.add(new Group(
                "Business Chama",
                8,
                12,
                320000,
                500000,
                false
        ));
        groupList.add(new Group(
                "School Fees",
                3,
                5,
                150000,
                200000,
                true
        ));

        groupAdapter.notifyDataSetChanged();

        // Set total balance
        double totalBalance = 125450;
        tvBalance.setText(String.format("KES %,.0f", totalBalance));

        // Set upcoming payout
        tvUpcomingPayout.setText("Sep 30, 2024");
    }

    private void setupListeners() {
        btnNewGroup.setOnClickListener(v -> {
            // Navigate to create group screen
            // startActivity(new Intent(requireContext(), CreateGroupActivity.class));
        });

        btnViewAll.setOnClickListener(v -> {
            // View all groups
        });

        btnLockFunds.setOnClickListener(v -> {
            // Lock funds functionality
        });
    }
}