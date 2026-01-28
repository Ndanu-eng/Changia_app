package com.changia.changia_app;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                UserEntity.class,
                GroupEntity.class,
                TransactionEntity.class,
                ContributionEntity.class,
                NotificationEntity.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "changia_database";
    private static volatile AppDatabase INSTANCE;

    // Executor for background tasks like pre-populating the admin
    private static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public abstract UserDao userDao();
    public abstract GroupDao groupDao();
    public abstract TransactionDao transactionDao();
    public abstract ContributionDao contributionDao();
    public abstract NotificationDao notificationDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DATABASE_NAME
                            )
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback) // Add the callback here
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Callback to pre-populate the database with an Admin user.
     */
    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                UserDao dao = INSTANCE.userDao();

                // Create the master Admin account
                UserEntity admin = new UserEntity();
                admin.setFullName("Changa Admin");
                admin.setEmail("admin@changia.com");
                admin.setPassword("admin123"); // In a real app, hash this!
                admin.setPhoneNumber("0700000000");
                admin.setWalletBalance(8025000.00);
                admin.setAdmin(true); // <--- THIS ACTIVATES THE MOCK DATA

                dao.insertUser(admin);
                Log.d("AppDatabase", "✅ Admin account pre-populated successfully");
            });
        }
    };

    public static void destroyInstance() {
        INSTANCE = null;
    }
}