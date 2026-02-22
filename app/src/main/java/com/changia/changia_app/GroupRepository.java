package com.changia.changia_app;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository for Group operations
 * Acts as a single source of truth for group data
 */
public class GroupRepository {

    private GroupDao groupDao;
    private ExecutorService executorService;

    // Callback interfaces
    public interface OnGroupInsertedListener {
        void onGroupInserted(long groupId);
    }

    public interface OnGroupUpdatedListener {
        void onGroupUpdated();
    }

    public interface OnGroupDeletedListener {
        void onGroupDeleted();
    }

    public interface OnGroupLoadedListener {
        void onGroupLoaded(GroupEntity group);
    }

    public GroupRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        groupDao = database.groupDao();
        // Use a fixed thread pool for database operations
        executorService = Executors.newFixedThreadPool(4);
    }

    /**
     * Insert a new group
     * @param group The group to insert
     * @param listener Callback with the inserted group ID
     */
    public void insert(GroupEntity group, OnGroupInsertedListener listener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                long id = groupDao.insertGroup(group);
                if (listener != null) {
                    listener.onGroupInserted(id);
                }
            }
        });
    }

    /**
     * Insert a new group without callback
     * @param group The group to insert
     */
    public void insert(GroupEntity group) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                groupDao.insertGroup(group);
            }
        });
    }

    /**
     * Update an existing group
     * @param group The group with updated information
     * @param listener Callback when update is complete
     */
    public void update(GroupEntity group, OnGroupUpdatedListener listener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                groupDao.updateGroup(group);
                if (listener != null) {
                    listener.onGroupUpdated();
                }
            }
        });
    }

    /**
     * Update an existing group without callback
     * @param group The group with updated information
     */
    public void update(GroupEntity group) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                groupDao.updateGroup(group);
            }
        });
    }

    /**
     * Delete a group
     * @param group The group to delete
     * @param listener Callback when delete is complete
     */
    public void delete(GroupEntity group, OnGroupDeletedListener listener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                groupDao.deleteGroup(group);
                if (listener != null) {
                    listener.onGroupDeleted();
                }
            }
        });
    }

    /**
     * Delete a group without callback
     * @param group The group to delete
     */
    public void delete(GroupEntity group) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                groupDao.deleteGroup(group);
            }
        });
    }

    /**
     * Get all groups for a user (LiveData - observes changes)
     * @param userId The user ID
     * @return LiveData list of groups
     */
    public LiveData<List<GroupEntity>> getUserGroups(int userId) {
        return groupDao.getGroupsByUser(userId);
    }

    /**
     * Get a specific group by ID (LiveData - observes changes)
     * @param groupId The group ID
     * @return LiveData group
     */
    public LiveData<GroupEntity> getGroupById(int groupId) {
        return groupDao.getGroupById(groupId);
    }

    /**
     * Get a specific group by ID (synchronous - one time fetch)
     * @param groupId The group ID
     * @param listener Callback with the group
     */
    public void getGroupByIdSync(int groupId, OnGroupLoadedListener listener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                GroupEntity group = groupDao.getGroupByIdSync(groupId);
                if (listener != null) {
                    listener.onGroupLoaded(group);
                }
            }
        });
    }

    /**
     * Add a contribution to a group
     * @param groupId The group ID
     * @param amount The contribution amount
     */
    public void addContribution(int groupId, double amount) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                groupDao.addContribution(groupId, amount);
            }
        });
    }

    /**
     * Add a contribution and update member count if new member
     * @param groupId The group ID
     * @param amount The contribution amount
     * @param isNewMember Whether this is from a new member
     */
    public void addContribution(int groupId, double amount, boolean isNewMember) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Add the contribution amount
                groupDao.addContribution(groupId, amount);

                // If new member, increment member count
                if (isNewMember) {
                    GroupEntity group = groupDao.getGroupByIdSync(groupId);
                    if (group != null) {
                        group.setCurrentMemberCount(group.getCurrentMemberCount() + 1);
                        groupDao.updateGroup(group);
                    }
                }
            }
        });
    }

    /**
     * Lock a group's funds
     * @param groupId The group ID
     * @param reason The reason for locking
     */
    public void lockGroup(int groupId, String reason) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                groupDao.setLockStatus(groupId, true);

                // Also update lock reason and date
                GroupEntity group = groupDao.getGroupByIdSync(groupId);
                if (group != null) {
                    group.setLockReason(reason);
                    group.setLockRequestDate(System.currentTimeMillis());
                    groupDao.updateGroup(group);
                }
            }
        });
    }

    /**
     * Unlock a group's funds
     * @param groupId The group ID
     */
    public void unlockGroup(int groupId) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                groupDao.setLockStatus(groupId, false);

                // Clear lock reason
                GroupEntity group = groupDao.getGroupByIdSync(groupId);
                if (group != null) {
                    group.setLockReason(null);
                    groupDao.updateGroup(group);
                }
            }
        });
    }

    /**
     * Get all groups (for admin)
     * @return LiveData list of all groups
     */
    public LiveData<List<GroupEntity>> getAllGroups() {
        return groupDao.getAllGroups();
    }

    /**
     * Get groups by status (locked/unlocked)
     * @param isLocked Lock status
     * @return LiveData list of groups
     */
    public LiveData<List<GroupEntity>> getGroupsByLockStatus(boolean isLocked) {
        return groupDao.getGroupsByLockStatus(isLocked);
    }

    /**
     * Search groups by name
     * @param searchTerm The search term
     * @return LiveData list of matching groups
     */
    public LiveData<List<GroupEntity>> searchGroups(String searchTerm) {
        return groupDao.searchGroups("%" + searchTerm + "%");
    }

    /**
     * Get total contributions across all user groups
     * @param userId The user ID
     * @return LiveData with total amount
     */
    public LiveData<Double> getTotalUserContributions(int userId) {
        return groupDao.getTotalUserContributions(userId);
    }

    /**
     * Shutdown the executor service
     * Call this in application's onTerminate or when repository is no longer needed
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}