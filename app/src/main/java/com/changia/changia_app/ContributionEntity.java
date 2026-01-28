package com.changia.changia_app;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * Represents a 'contributions' table in the Room database.
 * This links a user to a group for a specific contribution.
 */
@Entity(tableName = "contributions",
        foreignKeys = {
                @ForeignKey(entity = UserEntity.class,
                        parentColumns = "id",
                        childColumns = "user_id",
                        onDelete = ForeignKey.CASCADE), // If a user is deleted, their contributions are deleted
                @ForeignKey(entity = GroupEntity.class,
                        parentColumns = "id",
                        childColumns = "group_id",
                        onDelete = ForeignKey.CASCADE)  // If a group is deleted, its contributions are deleted
        })
public class ContributionEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id", index = true)
    private int userId;

    @ColumnInfo(name = "group_id", index = true)
    private int groupId;

    @ColumnInfo(name = "amount")
    private double amount;

    @ColumnInfo(name = "date")
    private long date; // Timestamp of the contribution

    // --- Constructor, Getters, and Setters ---

    public ContributionEntity(int userId, int groupId, double amount) {
        this.userId = userId;
        this.groupId = groupId;
        this.amount = amount;
        this.date = System.currentTimeMillis();
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getGroupId() { return groupId; }
    public double getAmount() { return amount; }
    public long getDate() { return date; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setDate(long date) { this.date = date; }
}
