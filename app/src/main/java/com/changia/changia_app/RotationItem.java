package com.changia.changia_app;

public class RotationItem {
    private String month;
    private String memberName;
    private String amount;
    private boolean isCompleted;

    public RotationItem(String month, String memberName, String amount, boolean isCompleted) {
        this.month = month;
        this.memberName = memberName;
        this.amount = amount;
        this.isCompleted = isCompleted;
    }

    public String getMonth() {
        return month;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getAmount() {
        return amount;
    }

    public boolean isCompleted() {
        return isCompleted;
    }
}