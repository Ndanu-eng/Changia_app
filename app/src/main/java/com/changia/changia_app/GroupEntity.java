package com.changia.changia_app;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

/**
 * Group Entity for Room Database
 * Represents a savings group/chama in the system
 */
@Entity(tableName = "groups")
public class GroupEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "target_amount")
    private double targetAmount;

    @ColumnInfo(name = "current_amount")
    private double currentAmount;

    @ColumnInfo(name = "target_member_count")
    private int targetMemberCount;

    @ColumnInfo(name = "current_member_count")
    private int currentMemberCount;

    @ColumnInfo(name = "monthly_contribution")
    private double monthlyContribution;

    @ColumnInfo(name = "contribution_frequency")
    private String contributionFrequency;

    @ColumnInfo(name = "created_by")
    private int createdBy;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "is_locked")
    private boolean isLocked;

    @ColumnInfo(name = "lock_reason")
    private String lockReason;

    @ColumnInfo(name = "lock_request_date")
    private long lockRequestDate;

    @ColumnInfo(name = "next_payout_date")
    private long nextPayoutDate;

    @ColumnInfo(name = "group_image")
    private String groupImage;

    // Default constructor
    public GroupEntity() {
        this.currentAmount = 0;
        this.currentMemberCount = 1;
        this.isLocked = false;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public int getTargetMemberCount() {
        return targetMemberCount;
    }

    public void setTargetMemberCount(int targetMemberCount) {
        this.targetMemberCount = targetMemberCount;
    }

    public int getCurrentMemberCount() {
        return currentMemberCount;
    }

    public void setCurrentMemberCount(int currentMemberCount) {
        this.currentMemberCount = currentMemberCount;
    }

    public double getMonthlyContribution() {
        return monthlyContribution;
    }

    public void setMonthlyContribution(double monthlyContribution) {
        this.monthlyContribution = monthlyContribution;
    }

    public String getContributionFrequency() {
        return contributionFrequency;
    }

    public void setContributionFrequency(String contributionFrequency) {
        this.contributionFrequency = contributionFrequency;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public String getLockReason() {
        return lockReason;
    }

    public void setLockReason(String lockReason) {
        this.lockReason = lockReason;
    }

    public long getLockRequestDate() {
        return lockRequestDate;
    }

    public void setLockRequestDate(long lockRequestDate) {
        this.lockRequestDate = lockRequestDate;
    }

    public long getNextPayoutDate() {
        return nextPayoutDate;
    }

    public void setNextPayoutDate(long nextPayoutDate) {
        this.nextPayoutDate = nextPayoutDate;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    // Helper methods
    public double getProgressPercentage() {
        if (targetAmount == 0) return 0;
        return (currentAmount / targetAmount) * 100;
    }

    public double getRemainingAmount() {
        return targetAmount - currentAmount;
    }

    public int getRemainingMembers() {
        return targetMemberCount - currentMemberCount;
    }

    public boolean isComplete() {
        return currentAmount >= targetAmount && currentMemberCount >= targetMemberCount;
    }

    @Override
    public String toString() {
        return "GroupEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", targetAmount=" + targetAmount +
                ", currentAmount=" + currentAmount +
                ", progress=" + getProgressPercentage() + "%" +
                '}';
    }
}