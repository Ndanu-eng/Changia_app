package com.changia.changia_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Manages user session data using SharedPreferences.
 * This class handles login, logout, and retrieval of user details.
 * The core login logic is now consolidated into a single createLoginSession method
 * to prevent data overwrites and ensure session integrity.
 */
public class SessionManager {

    private static final String PREF_NAME = "UserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_IS_ADMIN = "isAdmin";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_EMAIL = "userEmail";
    public static final String KEY_NAME = "userName";
    public static final String KEY_PHONE = "userPhone";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private final Context context;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Creates a login session from a UserEntity object.
     * This is the single source of truth for creating a user session. It stores the user's
     * login status, ID, details, and critically, their admin status in a single, atomic operation.
     *
     * @param user The UserEntity object for the user who just logged in.
     */
    public void createLoginSession(UserEntity user) {
        // Stage all user data for writing
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_NAME, user.getFullName());
        editor.putString(KEY_PHONE, user.getPhoneNumber());

        // --- THE CRITICAL FIX ---
        // Save the admin status directly from the user object in the same transaction.
        // This requires your UserEntity to have an isAdmin() method.
        editor.putBoolean(KEY_IS_ADMIN, user.isAdmin());

        // Commit all changes at once to prevent data loss.
        editor.commit();
    }

    /**
     * Retrieves the logged-in user's ID.
     *
     * @return The user's ID, or -1 if not found (which indicates no user is logged in).
     */
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Checks if the logged-in user has administrative privileges.
     * This now correctly returns the value saved during createLoginSession.
     *
     * @return true if the user is an admin, false otherwise.
     */
    public boolean isAdmin() {
        return sharedPreferences.getBoolean(KEY_IS_ADMIN, true);
    }

    /**
     * Retrieves the logged-in user's name.
     *
     * @return The user's name, or a default value "User" if not found.
     */
    public String getUserName() {
        return sharedPreferences.getString(KEY_NAME, "User");
    }

    /**
     * Retrieves the logged-in user's email.
     *
     * @return The user's email, or an empty string if not found.
     */
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    /**
     * Saves the user's full name and phone number to the session.
     * This is typically called after the user completes their profile.
     *
     * @param fullName    The user's full name.
     * @param phoneNumber The user's phone number.
     */
    public void saveProfileDetails(String fullName, String phoneNumber) {
        editor.putString(KEY_NAME, fullName);
        editor.putString(KEY_PHONE, phoneNumber);
        editor.commit();
    }

    /**
     * Clears all session data and redirects the user to the LoginActivity.
     * This effectively logs the user out.
     */
    public void logoutUser() {
        editor.clear();
        editor.commit();

        // Create an intent to go back to the LoginActivity
        Intent i = new Intent(context, LoginActivity.class);
        // Add flags to clear the activity stack and start a new task
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }

    /*
     * NOTE: The standalone setAdmin(boolean) method has been removed.
     * It is redundant and was the source of the original bug. The admin status should
     * only be set once during the login process via createLoginSession(user).
     */
}
