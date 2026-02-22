package com.changia.changia_app; // FIX: Corrected the package name to match the file path.

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Notification Entity for in-app notifications
 */
@Entity(tableName = "notifications",
        foreignKeys = @ForeignKey(entity = UserEntity.class,
                parentColumns = "id",
                childColumns = "user_id",
                onDelete = ForeignKey.CASCADE))
public class NotificationEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id", index = true)
    private int userId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "message")
    private String message;

    @ColumnInfo(name = "type")
    private String type; // "PAYOUT", "CONTRIBUTION", "GROUP_UPDATE", "PAYMENT", "SYSTEM"

    @ColumnInfo(name = "related_group_id")
    private Integer relatedGroupId; // Nullable

    @ColumnInfo(name = "is_read")
    private boolean isRead;

    @ColumnInfo(name = "action_url")
    private String actionUrl; // Deep link or screen to navigate

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    /**
     * Default constructor for creating new instances.
     * Ignored by Room so it uses the full constructor.
     */
    @Ignore
    public NotificationEntity() {
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }

    /**
     * Full constructor that Room will use to create objects from the database.
     */
    public NotificationEntity(int id, int userId, String title, String message, String type,
                              Integer relatedGroupId, boolean isRead, String actionUrl, long timestamp) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.relatedGroupId = relatedGroupId;
        this.isRead = isRead;
        this.actionUrl = actionUrl;
        this.timestamp = timestamp;
    }

    // --- Getters and Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getRelatedGroupId() { return relatedGroupId; }
    public void setRelatedGroupId(Integer relatedGroupId) { this.relatedGroupId = relatedGroupId; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getActionUrl() { return actionUrl; }
    public void setActionUrl(String actionUrl) { this.actionUrl = actionUrl; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
