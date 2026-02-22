package com.changia.changia_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class GroupsFragment extends Fragment {

    private RecyclerView rvGroups;
    private TextView tvNoGroups;
    private Button btnCreateGroup;
    private GroupAdapter groupAdapter;
    private List<Group> groupList;
    private SessionManager session;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);

        session = new SessionManager(requireContext());

        // Initialize Views
        rvGroups = view.findViewById(R.id.rv_groups);
        tvNoGroups = view.findViewById(R.id.tv_no_groups);
        btnCreateGroup = view.findViewById(R.id.btn_create_group);

        setupRecyclerView();
        loadGroups();

        return view;
    }

    private void setupRecyclerView() {
        groupList = new ArrayList<>();
        groupAdapter = new GroupAdapter(groupList, requireContext());
        rvGroups.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvGroups.setAdapter(groupAdapter);
    }

    private void loadGroups() {
        groupList.clear();

        if (session.isAdmin()) {
            // --- ADMIN: LOAD 8 UNIQUE MOCK GROUPS ---
            rvGroups.setVisibility(View.VISIBLE);
            tvNoGroups.setVisibility(View.GONE);

            groupList.add(new Group("💒 Nairobi Wedding Fund", 8, 10, 675000, 1000000, true));
            groupList.add(new Group("🚀 Tech Startup Investment", 12, 15, 1850000, 3000000, false));
            groupList.add(new Group("🏥 Emergency Medical Fund", 5, 8, 285000, 500000, true));
            groupList.add(new Group("📚 School Fees 2026", 6, 6, 480000, 600000, false));
            groupList.add(new Group("🏞️ Land Purchase - Kiambu", 10, 20, 3200000, 5000000, false));
            groupList.add(new Group("✈️ Vacation - Dubai", 4, 8, 125000, 400000, false));
            groupList.add(new Group("🏪 Small Business Capital", 7, 10, 560000, 800000, true));
            groupList.add(new Group("🚗 Car Purchase Fund", 9, 12, 850000, 1500000, false));
        } else {
            // --- NEW USER: BLANK STATE ---
            rvGroups.setVisibility(View.GONE);
            tvNoGroups.setVisibility(View.VISIBLE);
        }

        groupAdapter.notifyDataSetChanged();
    }
}