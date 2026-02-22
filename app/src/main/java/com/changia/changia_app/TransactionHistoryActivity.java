package com.changia.changia_app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionHistoryActivity extends AppCompatActivity {

    private static final String TAG = "TransactionHistory";

    // UI Components
    private RecyclerView rvTransactions;
    private TextView tvNoTransactions;

    // --- FIX: Add AppDatabase and SessionManager ---
    private AppDatabase appDatabase;
    private SessionManager sessionManager;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList; // Data source for the adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        // --- FIX: Initialize Room Database and SessionManager ---
        appDatabase = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initializeViews();
        setupRecyclerView();
        loadTransactionsFromDatabase(); // This method is now fully upgraded
    }

    private void initializeViews() {
        rvTransactions = findViewById(R.id.rv_transactions);
        tvNoTransactions = findViewById(R.id.tv_no_transactions);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        transactionList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactionList);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(transactionAdapter);
    }

    /**
     * --- THIS IS THE UPGRADED METHOD ---
     * It observes the database for the user's transaction history using LiveData.
     */
    private void loadTransactionsFromDatabase() {
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Log.e(TAG, "Cannot load transactions: Invalid user ID.");
            updateEmptyState(true);
            return;
        }

        // Get LiveData object from the DAO
        LiveData<List<TransactionEntity>> transactionsLiveData;

        if (sessionManager.isAdmin()) {
            // For an admin, you might want a different query, e.g., getAllTransactions().
            // For now, we'll just use the current user's transactions as an example.
            Log.d(TAG, "Admin user: Loading transactions for user ID " + userId);
            transactionsLiveData = appDatabase.transactionDao().getUserTransactions(userId);
        } else {
            Log.d(TAG, "Regular user: Loading transactions for user ID " + userId);
            transactionsLiveData = appDatabase.transactionDao().getUserTransactions(userId);
        }

        // Observe the LiveData for changes
        transactionsLiveData.observe(this, transactionEntities -> {
            Log.d(TAG, "Transaction data updated. Found " + transactionEntities.size() + " transactions.");
            transactionList.clear();

            for (TransactionEntity entity : transactionEntities) {
                // Map the TransactionEntity from the database to your Transaction UI model
                Transaction transaction = new Transaction(
                        entity.getDescription(),
                        formatAmount(entity.getAmount(), entity.getType()),
                        formatTimestamp(entity.getCreatedAt()),
                        entity.getType().equals("CREDIT")
                );
                transactionList.add(transaction);
            }
            transactionAdapter.notifyDataSetChanged();
            updateEmptyState(transactionList.isEmpty());
        });
    }

    /**
     * Shows or hides the "No transactions found" message.
     * @param isEmpty Whether the transaction list is empty.
     */
    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            tvNoTransactions.setVisibility(View.VISIBLE);
            rvTransactions.setVisibility(View.GONE);
        } else {
            tvNoTransactions.setVisibility(View.GONE);
            rvTransactions.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Formats a timestamp (long) into a user-friendly date string.
     */
    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    /**
     * Formats the transaction amount, adding a "+" for credits.
     */
    private String formatAmount(double amount, String type) {
        String formattedAmount = String.format("KES %,.2f", amount);
        if ("CREDIT".equals(type)) {
            return "+ " + formattedAmount;
        } else {
            return "- " + formattedAmount;
        }
    }
}
