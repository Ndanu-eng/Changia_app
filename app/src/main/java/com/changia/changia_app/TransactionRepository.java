package com.changia.changia_app;

import android.app.Application;
import androidx.lifecycle.LiveData;

// Import the correct Room components for Transactions
import com.changia.changia_app.AppDatabase;
import com.changia.changia_app.TransactionDao;
import com.changia.changia_app.TransactionEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository for Transaction operations.
 * This class abstracts the data source (Room database) from the rest of the app,
 * providing a clean API for transaction-related data.
 */
public class TransactionRepository {

    private TransactionDao transactionDao;
    private ExecutorService executorService;

    public TransactionRepository(Application application) {
        // Get the database instance and the appropriate DAO
        AppDatabase database = AppDatabase.getDatabase(application);
        transactionDao = database.transactionDao();
        // Create a background thread pool for write operations
        executorService = Executors.newFixedThreadPool(2);
    }

    /**
     * Inserts a new transaction on a background thread.
     * @param transaction The TransactionEntity to insert.
     */
    public void insertTransaction(TransactionEntity transaction) {
        executorService.execute(() -> transactionDao.insertTransaction(transaction));
    }

    /**
     * Fetches all transactions for a specific user as LiveData.
     * Room automatically handles running this query on a background thread.
     * @param userId The ID of the user.
     * @return A LiveData object wrapping the list of the user's transactions.
     */
    public LiveData<List<TransactionEntity>> getUserTransactions(int userId) {
        return transactionDao.getUserTransactions(userId);
    }

    /**
     * Fetches all transactions for a specific group as LiveData.
     * @param groupId The ID of the group.
     * @return A LiveData object wrapping the list of the group's transactions.
     */
    public LiveData<List<TransactionEntity>> getGroupTransactions(int groupId) {
        return transactionDao.getGroupTransactions(groupId);
    }
}
