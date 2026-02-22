package com.changia.changia_app;

public class Group {
    private int id;  // ADD THIS
    private String name;
    private int currentMembers;
    private int totalMembers;
    private double currentBalance;
    private double targetBalance;
    private boolean isLocked;

    // Constructor for mock data (without ID)
    public Group(String name, int currentMembers, int totalMembers,
                 double currentBalance, double targetBalance, boolean isLocked) {
        this.name = name;
        this.currentMembers = currentMembers;
        this.totalMembers = totalMembers;
        this.currentBalance = currentBalance;
        this.targetBalance = targetBalance;
        this.isLocked = isLocked;
    }

    // Constructor with ID for database
    public Group(int id, String name, int currentMembers, int totalMembers,
                 double currentBalance, double targetBalance, boolean isLocked) {
        this.id = id;
        this.name = name;
        this.currentMembers = currentMembers;
        this.totalMembers = totalMembers;
        this.currentBalance = currentBalance;
        this.targetBalance = targetBalance;
        this.isLocked = isLocked;
    }

    // Getters and setters
    public int getId() { return id; }  // ADD THIS
    public void setId(int id) { this.id = id; }  // ADD THIS

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