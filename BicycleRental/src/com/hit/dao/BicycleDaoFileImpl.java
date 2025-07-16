package com.hit.dao;

import com.hit.dm.Bicycle;

import java.util.Map;

public class BicycleDaoFileImpl implements IDao<Long, Bicycle> {
    private DataSourceManager dataManager;

    public BicycleDaoFileImpl() {
        this.dataManager = DataSourceManager.getInstance();
    }

    @Override
    public void save(Bicycle bicycle) {
        dataManager.getDataContainer().getBicycles().put(bicycle.getId(), bicycle);
        dataManager.saveData();
    }

    @Override
    public void delete(Bicycle bicycle) {
        if (search(bicycle.getId()) != null) {
            dataManager.getDataContainer().getBicycles().remove(bicycle.getId());
            dataManager.saveData();
        }

        else {
            throw new IllegalStateException("Bicycle does not exist.");
        }
    }

    @Override
    public Bicycle search(long id) {
        return dataManager.getDataContainer().getBicycles().get(id);
    }

    public Map<Long, Bicycle> getAllBicycles() {
        return dataManager.getDataContainer().getBicycles();
    }
}