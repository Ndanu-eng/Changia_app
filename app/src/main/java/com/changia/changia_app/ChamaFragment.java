package com.changia.changia_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chama, container, false);

        initializeViews(view);
        loadChamaData();
        setupRotationSchedule();
        setupPayoutHistory();

        return view;
    }

    private void initializeViews(View view) {
        tvCurrentMember = view.findViewById(R.id.tv_current_member);
        tvNextPayoutDate = view.findViewById(R.id.tv_next_payout_date);
        tvPayoutAmount = view.findViewById(R.id.tv_payout_amount);
        rvRotationSchedule = view.findViewById(R.id.rv_rotation_schedule);
        rvPayoutHistory = view.findViewById(R.id.rv_payout_history);
    }

    private void loadChamaData() {
        // Sample data - replace with API
        tvCurrentMember.setText("Current: Spencer Ngari");
        tvNextPayoutDate.setText("Next Payout: Oct 15, 2024");
        tvPayoutAmount.setText("KES 60,000");
    }

    private void setupRotationSchedule() {
        List<RotationItem> rotationList = new ArrayList<>();
        rotationList.add(new RotationItem("Sep 2024", "Betina Onyango", "KES 60,000", true));
        rotationList.add(new RotationItem("Oct 2024", "Spencer Ngari", "KES 60,000", false));
        rotationList.add(new RotationItem("Nov 2024", "Lorna Ndanu", "KES 60,000", false));
        rotationList.add(new RotationItem("Dec 2024", "John Doe", "KES 60,000", false));
        rotationList.add(new RotationItem("Jan 2025", "Jane Smith", "KES 60,000", false));

        rotationAdapter = new RotationAdapter(rotationList);
        rvRotationSchedule.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRotationSchedule.setAdapter(rotationAdapter);
    }

    private void setupPayoutHistory() {
        List<PayoutHistory> historyList = new ArrayList<>();
        historyList.add(new PayoutHistory("Aug 2024", "Jane Smith", "KES 60,000", true));
        historyList.add(new PayoutHistory("Jul 2024", "John Doe", "KES 60,000", true));
        historyList.add(new PayoutHistory("Jun 2024", "Lorna Ndanu", "KES 60,000", true));
        historyList.add(new PayoutHistory("May 2024", "Spencer Ngari", "KES 60,000", true));

        historyAdapter = new PayoutHistoryAdapter(historyList);
        rvPayoutHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPayoutHistory.setAdapter(historyAdapter);
    }
}