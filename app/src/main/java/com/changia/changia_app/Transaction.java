package com.changia.changia_app;

/**
 * Plain Old Java Object (POJO) representing a single transaction item
 * for display in the UI. This is what the TransactionAdapter will use.
 */
public class Transaction {

    private String description;
    private String amount; // Formatted amount (e.g., "+ KES 500.00")
    private String timestamp; // Formatted timestamp (e.g., "Jan 27, 2026")
    private boolean isCredit;

    public Transaction(String description, String amount, String timestamp, boolean isCredit) {
        this.description = description;
        this.amount = amount;
        this.timestamp = timestamp;
        this.isCredit = isCredit;
    }

    // Getters
    public String getDescription() {
        return description;
    }

    public String getAmount() {
        return amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isCredit() {
        return isCredit;
    }
}

