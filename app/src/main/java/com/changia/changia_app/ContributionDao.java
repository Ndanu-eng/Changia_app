package com.changia.changia_app;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.changia.changia_app.ContributionEntity;

import java.util.List;

@Dao
public interface ContributionDao {

    @Insert
    long insert(ContributionEntity contribution);

    @Update
    void update(ContributionEntity contribution);

    @Delete
    void delete(ContributionEntity contribution);

    // Get all contributions for a specific user
    @Query("SELECT * FROM contributions WHERE user_id = :userId")
    List<ContributionEntity> getContributionsByUser(int userId);

    // Get all contributions for a specific group
    @Query("SELECT * FROM contributions WHERE group_id = :groupId")
    List<ContributionEntity> getContributionsByGroup(int groupId);

    // Get contributions for a user in a specific group
    @Query("SELECT * FROM contributions WHERE user_id = :userId AND group_id = :groupId")
    List<ContributionEntity> getUserContributionsInGroup(int userId, int groupId);

    // Get total contributions amount for a user in a group
    @Query("SELECT SUM(amount) FROM contributions WHERE user_id = :userId AND group_id = :groupId")
    double getTotalContributionByUserInGroup(int userId, int groupId);

    // Get total contributions amount in a group
    @Query("SELECT SUM(amount) FROM contributions WHERE group_id = :groupId")
    double getTotalGroupContributions(int groupId);

    // Get contribution by ID
    @Query("SELECT * FROM contributions WHERE id = :id")
    ContributionEntity getContributionById(int id);

    // Delete contribution by ID
    @Query("DELETE FROM contributions WHERE id = :id")
    void deleteById(int id);

    // Get all contributions (for debugging or admin purposes)
    @Query("SELECT * FROM contributions")
    List<ContributionEntity> getAllContributions();

    // Get contributions within a date range
    @Query("SELECT * FROM contributions WHERE date BETWEEN :startDate AND :endDate")
    List<ContributionEntity> getContributionsByDateRange(long startDate, long endDate);

    // Clear all contributions (use with caution!)
    @Query("DELETE FROM contributions")
    void deleteAllContributions();
}
