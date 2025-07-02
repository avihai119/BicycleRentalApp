package com.hit.dao;

import com.hit.dm.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class DataSourceManager {
    private static final String DATA_SOURCE_FILE = "DataSource.txt";
    private static DataSourceManager instance;
    private Gson gson;
    private DataContainer dataContainer;

    private DataSourceManager() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        loadData();
    }

    public static synchronized DataSourceManager getInstance() {
        if (instance == null) {
            instance = new DataSourceManager();
        }
        return instance;
    }

    // ADD THIS METHOD FOR TESTING
    public static synchronized void resetInstance() {
        instance = null;
    }

    public DataContainer getDataContainer() {
        return dataContainer;
    }

    public void saveData() {
        try (FileWriter writer = new FileWriter(DATA_SOURCE_FILE)) {
            gson.toJson(dataContainer, writer);
        } catch (IOException e) {
            System.err.println("Error saving to DataSource.txt: " + e.getMessage());
        }
    }

    private void loadData() {
        File file = new File(DATA_SOURCE_FILE);
        if (!file.exists()) {
            dataContainer = new DataContainer();
            return; // Don't save empty file immediately
        }

        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<DataContainer>(){}.getType();
            dataContainer = gson.fromJson(reader, type);
            if (dataContainer == null) {
                dataContainer = new DataContainer();
            }
        } catch (IOException e) {
            System.err.println("Error loading from DataSource.txt: " + e.getMessage());
            dataContainer = new DataContainer();
        }
    }

    // Inner class to hold all data in one structure
    public static class DataContainer {
        private Map<Long, Bicycle> bicycles;
        private Map<Long, User> users;
        private Map<Long, Rental> rentals;

        public DataContainer() {
            this.bicycles = new HashMap<>();
            this.users = new HashMap<>();
            this.rentals = new HashMap<>();
        }

        public Map<Long, Bicycle> getBicycles() { return bicycles; }
        public Map<Long, User> getUsers() { return users; }
        public Map<Long, Rental> getRentals() { return rentals; }
    }
}