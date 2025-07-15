package com.hit.dao;

import com.hit.dm.User;

import java.util.Map;

public class UserDaoFileImpl implements IDao<Long, User> {
    private DataSourceManager dataManager;

    public UserDaoFileImpl() {
        this.dataManager = DataSourceManager.getInstance();
    }

    @Override
    public void save(User user) {
        // Allow updating existing users (overwrite if exists)
        dataManager.getDataContainer().getUsers().put(user.getId(), user);
        dataManager.saveData();
    }

    @Override
    public void delete(User user) {
        if (search(user.getId()) != null) {
            dataManager.getDataContainer().getUsers().remove(user.getId());
            dataManager.saveData();
        } else {
            throw new IllegalStateException("User does not exist.");
        }
    }

    @Override
    public User search(long id) {
        return dataManager.getDataContainer().getUsers().get(id);
    }

    public Map<Long, User> getAllUsers() {
        return dataManager.getDataContainer().getUsers();
    }
}