package com.hit.dao;

import com.hit.dm.Rental;
import java.util.Map;

public class RentalDaoFileImpl implements IDao<Long, Rental> {
    private DataSourceManager dataManager;

    public RentalDaoFileImpl() {
        this.dataManager = DataSourceManager.getInstance();
    }

    @Override
    public void save(Rental rental) {
        dataManager.getDataContainer().getRentals().put(rental.getRentalId(), rental);
        dataManager.saveData();
    }

    @Override
    public void delete(Rental rental) {
        if (search(rental.getRentalId()) != null) {
            dataManager.getDataContainer().getRentals().remove(rental.getRentalId());
            dataManager.saveData();
        }

        else {
            throw new IllegalStateException("The rental does not exist");
        }
    }

    @Override
    public Rental search(long id) {
        return dataManager.getDataContainer().getRentals().get(id);
    }

    public Rental findActiveRentalByBicycleId(long bicycleId) {
        for (Rental rental : dataManager.getDataContainer().getRentals().values()) {
            if (rental.getBicycleId() == bicycleId && rental.getEndTime() == null) {
                return rental;
            }
        }
        return null;
    }

    public Map<Long, Rental> getAllRentals() {
        return dataManager.getDataContainer().getRentals();
    }
}