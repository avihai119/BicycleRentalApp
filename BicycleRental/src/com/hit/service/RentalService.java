package com.hit.service;

import com.hit.dao.BicycleDaoFileImpl;
import com.hit.dao.RentalDaoFileImpl;
import com.hit.dao.UserDaoFileImpl;
import com.hit.dm.Bicycle;
import com.hit.dm.Rental;
import com.hit.dm.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RentalService {
    private BicycleDaoFileImpl bicycleDao;
    private UserDaoFileImpl userDao;
    private RentalDaoFileImpl rentalDao;
    private long rentalIdCounter = 1;

    public RentalService() {
        this.bicycleDao = new BicycleDaoFileImpl();
        this.userDao = new UserDaoFileImpl();
        this.rentalDao = new RentalDaoFileImpl();
        initializeRentalCounter();
    }

    public RentalService(BicycleDaoFileImpl bicycleDao, UserDaoFileImpl userDao, RentalDaoFileImpl rentalDao) {
        this.bicycleDao = bicycleDao;
        this.userDao = userDao;
        this.rentalDao = rentalDao;
        initializeRentalCounter();
    }

    private void initializeRentalCounter() {
        long maxId = rentalDao.getAllRentals().keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);
        rentalIdCounter = maxId + 1;
    }

    public Bicycle searchBicycle(long id) {
        return bicycleDao.search(id);
    }

    public User searchUser(long id) {
        return userDao.search(id);
    }

    public void addBicycle(Bicycle bicycle) {
        bicycleDao.save(bicycle);
    }

    public void addUser(User user) {
        userDao.save(user);
    }

    public List<Bicycle> getAllAvailableBicycles() {
        return bicycleDao.getAllBicycles().values().stream()
                .filter(bicycle -> !bicycle.isRented())
                .collect(Collectors.toList());
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers().values().stream().collect(Collectors.toList());
    }

    public Bicycle rentBicycle(long bicycleId, long userId) {
        Bicycle bicycle = bicycleDao.search(bicycleId);
        User user = userDao.search(userId);

        if (bicycle == null) {
            throw new IllegalArgumentException("Bicycle not found");
        }

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (bicycle.isRented()) {
            return null;
        }

        bicycle.setRented(true);
        bicycleDao.save(bicycle);

        Rental rental = new Rental(
                rentalIdCounter++,
                userId,
                bicycleId,
                LocalDateTime.now(),
                null
        );
        rentalDao.save(rental);

        return bicycle;
    }

    public boolean endRental(long bicycleId) {
        Bicycle bicycle = bicycleDao.search(bicycleId);

        if (bicycle == null) {
            return false;
        }

        if (!bicycle.isRented()) {
            return false; // Bicycle is not rented
        }

        Rental rental = rentalDao.findActiveRentalByBicycleId(bicycleId);
        if (rental == null) {
            return false;
        }

        rental.setEndTime(LocalDateTime.now());
        rentalDao.save(rental);

        bicycle.setRented(false);
        bicycleDao.save(bicycle);

        return true;
    }

    public List<Rental> getActiveRentals() {
        return rentalDao.getAllRentals().values().stream()
                .filter(rental -> rental.getEndTime() == null)
                .collect(Collectors.toList());
    }

    public List<Bicycle> getAllBicycles() {
        return bicycleDao.getAllBicycles().values().stream().collect(Collectors.toList());
    }

    public void deleteBicycle(Bicycle bicycle) {
        bicycleDao.delete(bicycle);
    }

    public void updateUser(User user) {
        userDao.save(user);
    }

    public void deleteUser(User user) {
        userDao.delete(user);
    }

    public boolean hasActiveRentals(long userId) {
        return rentalDao.getAllRentals().values().stream()
                .anyMatch(rental -> rental.getUserId() == userId && rental.getEndTime() == null);
    }

    public List<Rental> getUserRentalHistory(long userId) {
        return rentalDao.getAllRentals().values().stream()
                .filter(rental -> rental.getUserId() == userId)
                .collect(Collectors.toList());
    }

    public List<Rental> getAllRentals() {
        return rentalDao.getAllRentals().values().stream().collect(Collectors.toList());
    }

    public Rental searchRental(long rentalId) {
        return rentalDao.search(rentalId);
    }
}