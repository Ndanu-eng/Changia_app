package com.changia.changia_app;

import android.os.Bundle;
import android.util.Log;
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

public class WalletFragment extends Fragment {

    private TextView tvWalletBalance, tvUserName, tvNoTransactions;
    private RecyclerView rvTransactions;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        sessionManager = new SessionManager(requireContext());
        initializeViews(view);
        setupRecyclerView();
        loadData();

        return view;
    }

    private void initializeViews(View view) {
        tvWalletBalance = view.findViewById(R.id.tv_wallet_balance);
        tvUserName = view.findViewById(R.id.tv_wallet_user_name);
        tvNoTransactions = view.findViewById(R.id.tv_no_transactions);
        rvTransactions = view.findViewById(R.id.rv_transactions);

        tvUserName.setText(sessionManager.getUserName() + "'s Wallet 💰");
    }

    private void setupRecyclerView() {
        transactionList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactionList);
        rvTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTransactions.setAdapter(transactionAdapter);
    }

    private void loadData() {
        transactionList.clear();

        if (sessionManager.isAdmin()) {
            // ADMIN MOCK DATA
            tvWalletBalance.setText("KES 847,500");
            tvNoTransactions.setVisibility(View.GONE);
            rvTransactions.setVisibility(View.VISIBLE);

            transactionList.add(new Transaction("💵 Deposit from M-Pesa", "+KES 50,000", "Today, 3:45 PM", true));
            transactionList.add(new Transaction("📤 Payout to Tech Startup", "-KES 120,000", "Today, 11:20 AM", false));
            transactionList.add(new Transaction("🎯 Payout Received", "+KES 180,000", "Jan 23, 10:00 AM", true));
            transactionList.add(new Transaction("📱 Monthly App Subscription", "-KES 500", "Jan 20, 12:00 PM", false));
        } else {
            // NEW USER BLANK STATE
            tvWalletBalance.setText("KES 0.00");
            rvTransactions.setVisibility(View.GONE);
            tvNoTransactions.setVisibility(View.VISIBLE);
        }

        transactionAdapter.notifyDataSetChanged();
    }
}