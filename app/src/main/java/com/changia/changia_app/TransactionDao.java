package com.changia.changia_app;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TransactionDao {

    @Insert
    long insertTransaction(TransactionEntity transaction);

    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY created_at DESC")
    LiveData<List<TransactionEntity>> getUserTransactions(int userId);

    @Query("SELECT * FROM transactions WHERE group_id = :groupId ORDER BY created_at DESC")
    LiveData<List<TransactionEntity>> getGroupTransactions(int groupId);
}
