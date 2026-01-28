package com.changia.changia_app; // FIX: Corrected the package name to match the file path.

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Ignore; // Import the Ignore annotation

/**
 * User Entity for Room Database
 */
@Entity(tableName = "users")
public class UserEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "full_name")
    private String fullName;

    @ColumnInfo(name = "phone_number")
    private String phoneNumber;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "is_admin")
    private boolean isAdmin;

    @ColumnInfo(name = "wallet_balance")
    private double walletBalance;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    /**
     * IMPROVEMENT: This constructor is useful for creating new UserEntity objects in your code,
     * but Room should not use it. The @Ignore annotation tells Room to use a different constructor.
     */
    @Ignore
    public UserEntity() {
        this.createdAt = System.currentTimeMillis();
        this.walletBalance = 0.0;
        this.isAdmin = false; // It's good practice to set a default for booleans.
    }

    /**
     * This is the constructor that Room will use to create UserEntity objects
     * when reading data from the database.
     */
    public UserEntity(int id, String email, String fullName, String phoneNumber, String password, boolean isAdmin, double walletBalance, long createdAt) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.isAdmin = isAdmin;
        this.walletBalance = walletBalance;
        this.createdAt = createdAt;
    }


    // Getters and Setters (No changes needed here, they are perfect)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }

    public double getWalletBalance() { return walletBalance; }
    public void setWalletBalance(double walletBalance) { this.walletBalance = walletBalance; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
