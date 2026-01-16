package com.changia.changia_app;

public class PayoutHistory {
    private String month;
    private String recipient;
    private String amount;
    private boolean isPaid;

    public PayoutHistory(String month, String recipient, String amount, boolean isPaid) {
        this.month = month;
        this.recipient = recipient;
        this.amount = amount;
        this.isPaid = isPaid;
    }

    public String getMonth() {
        return month;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getAmount() {
        return amount;
    }

    public boolean isPaid() {
        return isPaid;
    }
}