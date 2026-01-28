package com.changia.changia_app;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * Represents a 'transactions' table in the Room database.
 */
@Entity(tableName = "transactions",
        foreignKeys = {
                @ForeignKey(entity = UserEntity.class,
                        parentColumns = "id",
                        childColumns = "user_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = GroupEntity.class,
                        parentColumns = "id",
                        childColumns = "group_id",
                        onDelete = ForeignKey.SET_NULL)
        })
public class TransactionEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id", index = true)
    private int userId;

    @ColumnInfo(name = "group_id", index = true)
    private Integer groupId; // Can be null if it's a personal wallet transaction

    @ColumnInfo(name = "description")
    private String description; // e.g., "Wallet Top Up", "Contribution to Wedding Fund"

    @ColumnInfo(name = "amount")
    private double amount;

    @ColumnInfo(name = "type")
    private String type; // "CREDIT" or "DEBIT"

    @ColumnInfo(name = "created_at")
    private long createdAt;

    // --- Constructor, Getters, and Setters ---

    public TransactionEntity(int userId, Integer groupId, String description, double amount, String type) {
        this.userId = userId;
        this.groupId = groupId;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public Integer getGroupId() { return groupId; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public long getCreatedAt() { return createdAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setGroupId(Integer groupId) { this.groupId = groupId; }
    public void setDescription(String description) { this.description = description; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setType(String type) { this.type = type; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
