package com.changia.changia_app;import android.app.Application;
import androidx.lifecycle.LiveData;

// --- FIX: Add the required imports for Room components ---
import com.changia.changia_app.AppDatabase;
import com.changia.changia_app.UserDao;
import com.changia.changia_app.UserEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository for User operations.
 * This class acts as a single source of truth for all user data,
 * abstracting the data source (Room database) from the rest of the app.
 */
public class UserRepository {

    private UserDao userDao;
    private ExecutorService executorService;

    public UserRepository(Application application) {
        // --- FIX: Use the correct method name 'getDatabase' ---
        AppDatabase database = AppDatabase.getDatabase(application);
        userDao = database.userDao();
        // Use a fixed thread pool for background database operations.
        executorService = Executors.newFixedThreadPool(2);
    }

    /**
     * Inserts a new user on a background thread.
     * @param user The UserEntity to insert.
     * @param listener A callback to return the ID of the newly inserted user.
     */
    public void insertUser(UserEntity user, OnUserInsertedListener listener) {
        executorService.execute(() -> {
            // The insert method in the DAO returns the new row ID.
            long userId = userDao.insertUser(user);
            if (listener != null) {
                // Return the result via the callback.
                listener.onUserInserted(userId);
            }
        });
    }

    /**
     * Updates an existing user on a background thread.
     * @param user The UserEntity with updated information.
     */
    public void updateUser(UserEntity user) {
        executorService.execute(() -> userDao.updateUser(user));
    }

    /**
     * Attempts to log in a user by checking credentials on a background thread.
     * @param email The user's email.
     * @param password The user's password.
     * @param listener A callback to return the UserEntity if login is successful, or null otherwise.
     */
    public void login(String email, String password, OnLoginListener listener) {
        executorService.execute(() -> {
            // Fetch the user from the database.
            UserEntity user = userDao.login(email, password);
            if (listener != null) {
                // Return the result via the callback.
                listener.onLoginResult(user);
            }
        });
    }

    /**
     * Fetches a user by their email on a background thread.
     * @param email The email to search for.
     * @param listener A callback to return the found UserEntity, or null if not found.
     */
    public void getUserByEmail(String email, OnUserFetchedListener listener) {
        executorService.execute(() -> {
            // Fetch the user from the database.
            UserEntity user = userDao.getUserByEmail(email);
            if (listener != null) {
                // Return the result via the callback.
                listener.onUserFetched(user);
            }
        });
    }

    /**
     * Gets a user by their ID and returns it as LiveData.
     * This is automatically managed by Room and does not need a background thread.
     * @param userId The ID of the user to fetch.
     * @return A LiveData object wrapping the UserEntity.
     */
    public LiveData<UserEntity> getUserByIdLive(int userId) {
        return userDao.getUserByIdLive(userId);
    }

    /**
     * Updates a user's wallet balance on a background thread.
     * @param userId The ID of the user.
     * @param balance The new balance to set.
     */
    public void updateWalletBalance(int userId, double balance) {
        executorService.execute(() -> userDao.updateWalletBalance(userId, balance));
    }


    // --- Callback Interfaces for Asynchronous Operations ---

    public interface OnUserInsertedListener {
        void onUserInserted(long userId);
    }

    public interface OnLoginListener {
        void onLoginResult(UserEntity user);
    }

    public interface OnUserFetchedListener {
        void onUserFetched(UserEntity user);
    }
}
