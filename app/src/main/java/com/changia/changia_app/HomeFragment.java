package com.changia.changia_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvBalance, tvUserName, tvNoGroups;
    private RecyclerView rvGroups;
    private GroupAdapter groupAdapter;
    private List<Group> groupList;
    private SessionManager session;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        session = new SessionManager(requireContext());
        tvBalance = view.findViewById(R.id.tv_balance);
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvNoGroups = view.findViewById(R.id.tv_no_groups);
        rvGroups = view.findViewById(R.id.rv_groups);

        setupRecyclerView();
        loadData();

        return view;
    }

    private void setupRecyclerView() {
        groupList = new ArrayList<>();
        groupAdapter = new GroupAdapter(groupList, requireContext());
        rvGroups.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvGroups.setAdapter(groupAdapter);
    }

    private void loadData() {
        groupList.clear();
        tvUserName.setText("Hi, " + session.getUserName());

        if (session.isAdmin()) {
            // --- ADMIN: LOAD MOCK DATA ---
            tvBalance.setText("KES 8,025,000");
            tvNoGroups.setVisibility(View.GONE);
            rvGroups.setVisibility(View.VISIBLE);

            groupList.add(new Group("💒 Nairobi Wedding Fund", 8, 10, 675000, 1000000, true));
            groupList.add(new Group("🚀 Tech Startup Investment", 12, 15, 1850000, 3000000, false));
            groupList.add(new Group("🏥 Emergency Medical Fund", 5, 8, 285000, 500000, true));
        } else {
            // --- NEW USER: SHOW BLANK STATE ---
            tvBalance.setText("KES 0");
            rvGroups.setVisibility(View.GONE);
            tvNoGroups.setVisibility(View.VISIBLE);
        }

        groupAdapter.notifyDataSetChanged();
    }
}