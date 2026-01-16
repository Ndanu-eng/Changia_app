package com.changia.changia_app;

public class ActivityItem {
    private String description;
    private String timestamp;

    public ActivityItem(String description, String timestamp) {
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public String getTimestamp() {
        return timestamp;
    }
}