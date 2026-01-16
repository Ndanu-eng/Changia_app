package com.changia.changia_app;

public class Group {
    private String name;
    private int currentMembers;
    private int totalMembers;
    private double currentBalance;
    private double targetBalance;
    private boolean isLocked;

    public Group(String name, int currentMembers, int totalMembers,
                 double currentBalance, double targetBalance, boolean isLocked) {
        this.name = name;
        this.currentMembers = currentMembers;
        this.totalMembers = totalMembers;
        this.currentBalance = currentBalance;
        this.targetBalance = targetBalance;
        this.isLocked = isLocked;
    }

    // Getters and setters
    public String getName() { return name; }
    public int getCurrentMembers() { return currentMembers; }
    public int getTotalMembers() { return totalMembers; }
    public double getCurrentBalance() { return currentBalance; }
    public double getTargetBalance() { return targetBalance; }
    public boolean isLocked() { return isLocked; }

    public double getProgressPercentage() {
        if (targetBalance == 0) return 0;
        return (currentBalance / targetBalance) * 100;
    }
}