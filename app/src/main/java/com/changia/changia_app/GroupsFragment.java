package com.changia.changia_app;

import android.content.Intent;
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

public class GroupsFragment extends Fragment {

    private RecyclerView rvGroups;
    private Button btnCreateGroup;
    private TextView tvNoGroups;
    private GroupAdapter groupAdapter;
    private List<Group> groupList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);

        initializeViews(view);
        setupRecyclerView();
        loadGroups();
        setupListeners();

        return view;
    }

    private void initializeViews(View view) {
        rvGroups = view.findViewById(R.id.rv_groups);
        btnCreateGroup = view.findViewById(R.id.btn_create_group);
        tvNoGroups = view.findViewById(R.id.tv_no_groups);
    }

    private void setupRecyclerView() {
        groupList = new ArrayList<>();
        groupAdapter = new GroupAdapter(groupList, requireContext());

        rvGroups.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvGroups.setAdapter(groupAdapter);

        // Set click listener for groups
        groupAdapter.setOnItemClickListener(new GroupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Group group = groupList.get(position);
                Intent intent = new Intent(requireContext(), GroupDetailsActivity.class);
                intent.putExtra("group_id", group.getName()); // Pass group ID
                startActivity(intent);
            }
        });
    }

    private void loadGroups() {
        // Sample data - replace with API call
        int oldSize = groupList.size();
        groupList.clear();

        if (oldSize > 0) {
            groupAdapter.notifyItemRangeRemoved(0, oldSize);
        }

        groupList.add(new Group("Wedding Fund", 6, 10, 540000, 750000, true));
        groupList.add(new Group("Business Chama", 8, 12, 320000, 500000, false));
        groupList.add(new Group("School Fees", 3, 5, 150000, 200000, true));
        groupList.add(new Group("Car Fund", 4, 8, 200000, 400000, false));

        groupAdapter.notifyItemRangeInserted(0, groupList.size());

        // Show/hide empty state
        if (groupList.isEmpty()) {
            tvNoGroups.setVisibility(View.VISIBLE);
            rvGroups.setVisibility(View.GONE);
        } else {
            tvNoGroups.setVisibility(View.GONE);
            rvGroups.setVisibility(View.VISIBLE);
        }
    }

    private void setupListeners() {
        btnCreateGroup.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), CreateGroupActivity.class))
        );
    }
}