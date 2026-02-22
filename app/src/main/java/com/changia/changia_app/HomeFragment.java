package com.changia.changia_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView tvBalance;
    private TextView tvUserName;
    private TextView tvUpcomingPayout;
    private TextView tvNoGroups;
    private RecyclerView rvGroups;
    private Button btnNewGroup;
    private Button btnViewAll;
    private Button btnLockFunds;

    private GroupAdapter groupAdapter;
    private List<Group> groupList;
    private SessionManager sessionManager;
    private AppDatabase appDatabase;
    private GroupRepository groupRepository;
    private int userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize
        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();
        appDatabase = AppDatabase.getDatabase(requireContext());
        groupRepository = new GroupRepository(requireActivity().getApplication());
        groupList = new ArrayList<>();

        // Initialize views
        tvBalance = view.findViewById(R.id.tv_balance);
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUpcomingPayout = view.findViewById(R.id.tv_upcoming_payout);
        tvNoGroups = view.findViewById(R.id.tv_no_groups);
        rvGroups = view.findViewById(R.id.rv_groups);
        btnNewGroup = view.findViewById(R.id.btn_new_group);
        btnViewAll = view.findViewById(R.id.btn_view_all);
        btnLockFunds = view.findViewById(R.id.btn_lock_funds);

        setupRecyclerView();
        setupListeners();

        // Load REAL data from database
        loadUserData();
        loadGroups();

        return view;
    }

    private void setupRecyclerView() {
        groupAdapter = new GroupAdapter(groupList, requireContext());
        rvGroups.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvGroups.setAdapter(groupAdapter);

        groupAdapter.setOnItemClickListener(new GroupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Group clickedGroup = groupList.get(position);
                Intent intent = new Intent(requireContext(), GroupDetailsActivity.class);

                // Pass group ID if available
                if (clickedGroup.getId() > 0) {
                    intent.putExtra("group_id", clickedGroup.getId());
                }

                intent.putExtra("group_name", clickedGroup.getName());
                intent.putExtra("group_current", clickedGroup.getCurrentBalance());
                intent.putExtra("group_target", clickedGroup.getTargetBalance());
                intent.putExtra("group_current_members", clickedGroup.getCurrentMembers());
                intent.putExtra("group_total_members", clickedGroup.getTotalMembers());
                intent.putExtra("group_locked", clickedGroup.isLocked());
                startActivity(intent);
            }
        });
    }

    private void setupListeners() {
        btnNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), CreateGroupActivity.class);
                startActivity(intent);
            }
        });

        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_nav);
                    if (bottomNav != null) {
                        bottomNav.setSelectedItemId(R.id.groupsFragment);
                    }
                }
            }
        });

        btnLockFunds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupList.isEmpty()) {
                    Snackbar.make(requireView(), "Create a group first to lock funds", Snackbar.LENGTH_LONG)
                            .setAction("Create", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(requireContext(), CreateGroupActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .show();
                } else {
                    // Find first unlocked group
                    Group groupToLock = null;
                    for (Group group : groupList) {
                        if (!group.isLocked()) {
                            groupToLock = group;
                            break;
                        }
                    }

                    if (groupToLock != null) {
                        Intent intent = new Intent(requireContext(), LockRequestActivity.class);

                        // Pass group ID if available
                        if (groupToLock.getId() > 0) {
                            intent.putExtra("group_id", groupToLock.getId());
                        }

                        intent.putExtra("group_name", groupToLock.getName());
                        intent.putExtra("group_amount", groupToLock.getCurrentBalance());
                        startActivity(intent);
                    } else {
                        Toast.makeText(requireContext(), "All your groups are already locked", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // Load REAL user data from database
    private void loadUserData() {
        if (userId == -1) {
            tvUserName.setText("Hi, Guest");
            tvBalance.setText("KES 0");
            return;
        }

        // Observe user data from database (auto-updates when balance changes)
        LiveData<UserEntity> userLiveData = appDatabase.userDao().getUserByIdLive(userId);
        userLiveData.observe(getViewLifecycleOwner(), new Observer<UserEntity>() {
            @Override
            public void onChanged(UserEntity user) {
                if (user != null) {
                    tvUserName.setText("Hi, " + user.getFullName());

                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "KE"));
                    String balanceFormatted = formatter.format(user.getWalletBalance()).replace("KSh", "KES");
                    tvBalance.setText(balanceFormatted);

                    // Set upcoming payout
                    tvUpcomingPayout.setText("Next payout: " + getFormattedDate());
                }
            }
        });
    }

    // Load REAL groups from database
    private void loadGroups() {
        if (userId == -1) return;

        groupRepository.getUserGroups(userId).observe(getViewLifecycleOwner(), new Observer<List<GroupEntity>>() {
            @Override
            public void onChanged(List<GroupEntity> groupEntities) {
                if (groupEntities != null && !groupEntities.isEmpty()) {
                    // Convert GroupEntity to Group for adapter
                    groupList.clear();
                    for (GroupEntity entity : groupEntities) {
                        // Use constructor with ID
                        Group group = new Group(
                                entity.getId(),              // Pass the ID
                                entity.getName(),
                                entity.getCurrentMemberCount(),
                                entity.getTargetMemberCount(),
                                entity.getCurrentAmount(),
                                entity.getTargetAmount(),
                                entity.isLocked()
                        );
                        groupList.add(group);
                    }

                    groupAdapter.notifyDataSetChanged();
                    rvGroups.setVisibility(View.VISIBLE);
                    tvNoGroups.setVisibility(View.GONE);
                } else {
                    rvGroups.setVisibility(View.GONE);
                    tvNoGroups.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private String getFormattedDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        long nextWeek = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000);
        return sdf.format(new java.util.Date(nextWeek));
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when returning to fragment
        loadUserData();
        loadGroups();
    }
}