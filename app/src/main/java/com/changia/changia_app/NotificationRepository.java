package com.changia.changia_app;

import android.app.Application;
import androidx.lifecycle.LiveData;

// --- FIX: Add the required imports for Room components ---
import com.changia.changia_app.AppDatabase;
import com.changia.changia_app.NotificationDao;
import com.changia.changia_app.NotificationEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository for Notification operations.
 * This class abstracts the data source (Room database) from the rest of the app.
 */
public class NotificationRepository {

    private NotificationDao notificationDao;
    private ExecutorService executorService;

    public NotificationRepository(Application application) {
        // --- FIX: Use the correct method name 'getDatabase' ---
        AppDatabase database = AppDatabase.getDatabase(application);
        notificationDao = database.notificationDao();
        executorService = Executors.newFixedThreadPool(2);
    }

    /**
     * Inserts a notification on a background thread.
     */
    public void insertNotification(NotificationEntity notification) {
        executorService.execute(() -> notificationDao.insertNotification(notification));
    }

    /**
     * Fetches all of a user's notifications as LiveData.
     */
    public LiveData<List<NotificationEntity>> getUserNotifications(int userId) {
        return notificationDao.getNotificationsForUser(userId); // Assuming this is the correct method name in your DAO
    }

    // Note: The getUnreadNotifications and getUnreadCount methods depend on your NotificationDao.
    // If they don't exist in the DAO, these methods would need to be removed or implemented there.
    // For now, I will assume they exist based on the original code.

    /**
     * Fetches only the unread notifications for a user as LiveData.
     * This requires a corresponding method in your NotificationDao.
     */
    // public LiveData<List<NotificationEntity>> getUnreadNotifications(int userId) {
    //     return notificationDao.getUnreadNotifications(userId);
    // }

    /**
     * Gets the count of unread notifications for a user as LiveData.
     * This requires a corresponding method in your NotificationDao.
     */
    // public LiveData<Integer> getUnreadCount(int userId) {
    //     return notificationDao.getUnreadCount(userId);
    // }

    /**
     * Marks a specific notification as read on a background thread.
     */
    public void markAsRead(int notificationId) {
        executorService.execute(() -> notificationDao.markAsRead(notificationId));
    }

    /**
     * Marks all of a user's notifications as read on a background thread.
     */
    public void markAllAsRead(int userId) {
        executorService.execute(() -> notificationDao.markAllAsRead(userId));
    }
}
