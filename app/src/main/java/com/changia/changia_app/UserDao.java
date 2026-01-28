package com.changia.changia_app; // Corrected package name

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {

    @Insert
    long insertUser(UserEntity user);

    @Update
    void updateUser(UserEntity user);

    @Delete
    void deleteUser(UserEntity user);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    UserEntity login(String email, String password);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    UserEntity getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE id = :userId")
    UserEntity getUserById(int userId);

    @Query("SELECT * FROM users WHERE id = :userId")
    LiveData<UserEntity> getUserByIdLive(int userId);

    @Query("UPDATE users SET wallet_balance = :balance WHERE id = :userId")
    void updateWalletBalance(int userId, double balance);

    @Query("SELECT wallet_balance FROM users WHERE id = :userId")
    double getWalletBalance(int userId);
}

