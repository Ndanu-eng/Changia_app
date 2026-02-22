package com.changia.changia_app;

import android.app.Application;
import androidx.lifecycle.LiveData;

// --- FIX: These imports are now correct ---
import com.changia.changia_app.AppDatabase;
import com.changia.changia_app.GroupDao;
import com.changia.changia_app.GroupEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroupRepository {

    private GroupDao groupDao;
    private ExecutorService executorService;

    public GroupRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        groupDao = database.groupDao();
        executorService = Executors.newFixedThreadPool(2);
    }

    /**
     * Inserts a new group on a background thread.
     * @param groupEntity The GroupEntity to insert.
     * @param listener    An optional callback to return the new group's ID.
     */
    // --- FIX: Changed parameter name from 'GroupEntity' to 'groupEntity' ---
    public void insertGroup(GroupEntity groupEntity, OnGroupInsertedListener listener) {
        executorService.execute(() -> {
            long groupId = groupDao.insertGroup(groupEntity);
            if (listener != null) {
                listener.onGroupInserted(groupId);
            }
        });
    }

    /**
     * Updates an existing group on a background thread.
     * @param groupEntity The GroupEntity with updated information.
     */
    // --- FIX: Changed parameter name from 'GroupEntity' to 'groupEntity' ---
    public void updateGroup(GroupEntity groupEntity) {
        executorService.execute(() -> groupDao.updateGroup(groupEntity));
    }

    /**
     * Deletes a group on a background thread.
     * @param group The GroupEntity to delete.
     */
    public void deleteGroup(GroupEntity group) {
        executorService.execute(() -> groupDao.deleteGroup(group));
    }

    // --- No changes needed below this line, they are correct ---

    public LiveData<List<GroupEntity>> getAllGroups() {
        return groupDao.getAllGroups();
    }

    public LiveData<GroupEntity> getGroupById(int groupId) {
        return groupDao.getGroupById(groupId);
    }

    public LiveData<List<GroupEntity>> getGroupsByUser(int userId) {
        return groupDao.getGroupsByUser(userId);
    }

    public void addToGroupBalance(int groupId, double amount) {
        executorService.execute(() -> groupDao.addToGroupBalance(groupId, amount));
    }

    public void lockGroup(int groupId, boolean locked) {
        executorService.execute(() -> groupDao.lockGroup(groupId, locked));
    }

    public LiveData<Double> getTotalBalance() {
        return groupDao.getTotalBalance();
    }

    public interface OnGroupInsertedListener {
        void onGroupInserted(long groupId);
    }
}
