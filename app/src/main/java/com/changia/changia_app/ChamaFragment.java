package com.changia.changia_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ChamaFragment extends Fragment {

    private TextView tvCurrentMember, tvNextPayoutDate, tvPayoutAmount;
    private RecyclerView rvRotationSchedule, rvPayoutHistory;
    private RotationAdapter rotationAdapter;
    private PayoutHistoryAdapter historyAdapter;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chama, container, false);

        sessionManager = new SessionManager(requireContext());
        initializeViews(view);

        if (sessionManager.isAdmin()) {
            loadAdminMockData();
        } else {
            loadEmptyState();
        }

        return view;
    }

    private void initializeViews(View view) {
        tvCurrentMember = view.findViewById(R.id.tv_current_member);
        tvNextPayoutDate = view.findViewById(R.id.tv_next_payout_date);
        tvPayoutAmount = view.findViewById(R.id.tv_payout_amount);
        rvRotationSchedule = view.findViewById(R.id.rv_rotation_schedule);
        rvPayoutHistory = view.findViewById(R.id.rv_payout_history);
    }

    private void loadAdminMockData() {
        // Card Data
        tvCurrentMember.setText("Spencer Ngari");
        tvNextPayoutDate.setText("Next Payout: Feb 15, 2026");
        tvPayoutAmount.setText("KES 60,000");

        // Rotation Schedule
        List<RotationItem> rotationList = new ArrayList<>();
        rotationList.add(new RotationItem("Jan 2026", "Betina Onyango", "KES 60,000", true));
        rotationList.add(new RotationItem("Feb 2026", "Spencer Ngari", "KES 60,000", false));
        rotationList.add(new RotationItem("Mar 2026", "Lorna Ndanu", "KES 60,000", false));

        rotationAdapter = new RotationAdapter(rotationList);
        rvRotationSchedule.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRotationSchedule.setAdapter(rotationAdapter);

        // Payout History
        List<PayoutHistory> historyList = new ArrayList<>();
        historyList.add(new PayoutHistory("Dec 2025", "Jane Smith", "KES 60,000", true));
        historyList.add(new PayoutHistory("Nov 2025", "John Doe", "KES 60,000", true));

        historyAdapter = new PayoutHistoryAdapter(historyList);
        rvPayoutHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPayoutHistory.setAdapter(historyAdapter);
    }

    private void loadEmptyState() {
        tvCurrentMember.setText("No Active Rotation");
        tvNextPayoutDate.setText("Join a group to see schedules");
        tvPayoutAmount.setText("KES 0");

        rvRotationSchedule.setVisibility(View.GONE);
        rvPayoutHistory.setVisibility(View.GONE);
    }
}