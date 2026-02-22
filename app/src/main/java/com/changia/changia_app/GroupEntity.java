package com.changia.changia_app;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "groups") // The table name itself is not the issue
public class GroupEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "current_members")
    private int currentMembers;

    @ColumnInfo(name = "total_members")
    private int totalMembers;

    @ColumnInfo(name = "current_balance")
    private double currentBalance;

    @ColumnInfo(name = "target_balance")
    private double targetBalance;

    @ColumnInfo(name = "is_locked")
    private boolean isLocked;

    @ColumnInfo(name = "contribution_amount")
    private double contributionAmount;

    @ColumnInfo(name = "contribution_frequency")
    private String contributionFrequency;

    @ColumnInfo(name = "next_payout_date")
    private long nextPayoutDate;

    @ColumnInfo(name = "created_by", index = true) // Indexing foreign keys is good practice
    private int createdBy;

    @ColumnInfo(name = "createdAt")
    private long createdAt;


    // Full constructor that Room uses.
    // Having one single, unambiguous constructor for Room is the most stable approach.
    public GroupEntity(int id, String name, String description, int currentMembers, int totalMembers,
                       double currentBalance, double targetBalance, boolean isLocked, double contributionAmount,
                       String contributionFrequency, long nextPayoutDate, int createdBy, long createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.currentMembers = currentMembers;
        this.totalMembers = totalMembers;
        this.currentBalance = currentBalance;
        this.targetBalance = targetBalance;
        this.isLocked = isLocked;
        this.contributionAmount = contributionAmount;
        this.contributionFrequency = contributionFrequency;
        this.nextPayoutDate = nextPayoutDate;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    // A separate, ignored constructor for creating new objects in code.
    @Ignore
    public GroupEntity(String name, String description, int totalMembers, double targetBalance,
                       double contributionAmount, String contributionFrequency, int createdBy) {
        this.name = name;
        this.description = description;
        this.totalMembers = totalMembers;
        this.targetBalance = targetBalance;
        this.contributionAmount = contributionAmount;
        this.contributionFrequency = contributionFrequency;
        this.createdBy = createdBy;
        // Set defaults for a new group
        this.createdAt = System.currentTimeMillis();
        this.currentBalance = 0.0;
        this.currentMembers = 1; // The creator
        this.isLocked = false;
    }

    // --- Getters and Setters ---
    // (Your existing getters and setters are fine, no changes needed here)

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getCurrentMembers() { return currentMembers; }
    public void setCurrentMembers(int currentMembers) { this.currentMembers = currentMembers; }
    public int getTotalMembers() { return totalMembers; }
    public void setTotalMembers(int totalMembers) { this.totalMembers = totalMembers; }
    public double getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(double currentBalance) { this.currentBalance = currentBalance; }
    public double getTargetBalance() { return targetBalance; }
    public void setTargetBalance(double targetBalance) { this.targetBalance = targetBalance; }
    public boolean isLocked() { return isLocked; }
    public void setLocked(boolean locked) { isLocked = locked; }
    public double getContributionAmount() { return contributionAmount; }
    public void setContributionAmount(double contributionAmount) { this.contributionAmount = contributionAmount; }
    public String getContributionFrequency() { return contributionFrequency; }
    public void setContributionFrequency(String contributionFrequency) { this.contributionFrequency = contributionFrequency; }
    public long getNextPayoutDate() { return nextPayoutDate; }
    public void setNextPayoutDate(long nextPayoutDate) { this.nextPayoutDate = nextPayoutDate; }
    public int getCreatedBy() { return createdBy; }
    void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}