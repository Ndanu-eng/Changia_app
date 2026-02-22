package com.changia.changia_app;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface GroupDao {

    @Insert
    long insertGroup(GroupEntity group);

    @Update
    void updateGroup(GroupEntity group);

    @Delete
    void deleteGroup(GroupEntity group);

    // Using exact column names with underscores
    @Query("SELECT * FROM `groups` WHERE created_by = :userId ORDER BY created_at DESC")
    LiveData<List<GroupEntity>> getGroupsByUser(int userId);

    @Query("SELECT * FROM `groups` WHERE id = :groupId")
    LiveData<GroupEntity> getGroupById(int groupId);

    @Query("SELECT * FROM `groups` WHERE id = :groupId")
    GroupEntity getGroupByIdSync(int groupId);

    @Query("UPDATE `groups` SET current_amount = current_amount + :amount WHERE id = :groupId")
    void addContribution(int groupId, double amount);

    @Query("UPDATE `groups` SET is_locked = :locked WHERE id = :groupId")
    void setLockStatus(int groupId, boolean locked);

    @Query("SELECT * FROM `groups` ORDER BY created_at DESC")
    LiveData<List<GroupEntity>> getAllGroups();

    @Query("SELECT * FROM `groups` WHERE is_locked = :isLocked ORDER BY created_at DESC")
    LiveData<List<GroupEntity>> getGroupsByLockStatus(boolean isLocked);

    @Query("SELECT * FROM `groups` WHERE name LIKE :searchTerm ORDER BY created_at DESC")
    LiveData<List<GroupEntity>> searchGroups(String searchTerm);

    @Query("SELECT SUM(current_amount) FROM `groups` WHERE created_by = :userId")
    LiveData<Double> getTotalUserContributions(int userId);
}