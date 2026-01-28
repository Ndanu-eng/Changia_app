package com.changia.changia_app;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// This import is correct
import com.changia.changia_app.GroupEntity;

@Dao
public interface GroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertGroup(GroupEntity group);

    @Update
    void updateGroup(GroupEntity group);

    @Delete
    void deleteGroup(GroupEntity group);

    @Query("SELECT * FROM groups ORDER BY createdAt DESC")
    LiveData<List<GroupEntity>> getAllGroups();

    @Query("SELECT * FROM groups WHERE id = :groupId")
    LiveData<GroupEntity> getGroupById(int groupId);

    @Query("SELECT * FROM groups WHERE created_by = :userId ORDER BY createdAt DESC")
    LiveData<List<GroupEntity>> getGroupsByUser(int userId);

    @Query("UPDATE groups SET current_balance = current_balance + :amount WHERE id = :groupId")
    void addToGroupBalance(int groupId, double amount);

    @Query("UPDATE groups SET is_locked = :locked WHERE id = :groupId")
    void lockGroup(int groupId, boolean locked);

    @Query("SELECT SUM(current_balance) FROM groups")
    LiveData<Double> getTotalBalance();
}
