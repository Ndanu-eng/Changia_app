package com.changia.changia_app;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NotificationDao {

    @Insert
    long insertNotification(NotificationEntity notification);

    @Update
    void updateNotification(NotificationEntity notification);

    // FIXED: Changed created_at to timestamp to match NotificationEntity
    @Query("SELECT * FROM notifications WHERE user_id = :userId ORDER BY timestamp DESC")
    LiveData<List<NotificationEntity>> getNotificationsForUser(int userId);

    @Query("UPDATE notifications SET is_read = 1 WHERE id = :notificationId")
    void markAsRead(int notificationId);

    @Query("UPDATE notifications SET is_read = 1 WHERE user_id = :userId")
    void markAllAsRead(int userId);

    @Query("DELETE FROM notifications WHERE id = :notificationId")
    void deleteNotification(int notificationId);
}