package com.hit.server;

import com.hit.controller.BicycleController;
import com.hit.controller.RentalController;
import com.hit.controller.UserController;
import com.hit.service.RentalService;
import com.hit.dm.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server implements Runnable {
    private int port;
    private ServerSocket serverSocket;
    private Map<String, Object> controllers;
    private boolean isRunning = false;
    private RentalService rentalService;

    public Server(int port) {
        this.port = port;
        this.controllers = new HashMap<>();
        initializeServices();
        initializeControllers();
        initializeBicycles();
    }

    private void initializeServices() {
        this.rentalService = new RentalService();
        System.out.println("Rental service initialized");
    }

    private void initializeControllers() {
        try {
            BicycleController bicycleController = new BicycleController(rentalService);
            controllers.put("bicyclecontroller", bicycleController);
            System.out.println("Bicycle controller initialized");

            RentalController rentalController = new RentalController(rentalService);
            controllers.put("rentalcontroller", rentalController);
            System.out.println("Rental controller initialized");

            UserController userController = new UserController(rentalService);
            controllers.put("usercontroller", userController);
            System.out.println("User controller initialized");

            System.out.println("All controllers initialized successfully");

        }

        catch (Exception e) {
            System.err.println("Error initializing controllers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeBicycles() {
        System.out.println("Initializing bicycle inventory...");

        try {
            System.out.println("Adding regular bicycles to inventory...");
            Bicycle bike1 = new Bicycle(101, "Trek Mountain Bike", 21, 12.5, Color.BLUE);
            Bicycle bike2 = new Bicycle(102, "Giant Road Bike", 18, 11.0, Color.RED);
            Bicycle bike3 = new Bicycle(103, "Specialized Hybrid", 24, 13.2, Color.BLACK);
            Bicycle bike4 = new Bicycle(104, "Cannondale Trail", 27, 14.0, Color.WHITE);
            Bicycle bike5 = new Bicycle(105, "Scott City Bike", 7, 10.5, Color.BLUE);
            Bicycle bike6 = new Bicycle(106, "GT Mountain", 21, 13.8, Color.RED);
            Bicycle bike7 = new Bicycle(107, "Merida Road", 16, 9.8, Color.BLACK);
            Bicycle bike8 = new Bicycle(108, "Cube Hybrid", 24, 12.0, Color.WHITE);

            addBicycleSafely(bike1);
            addBicycleSafely(bike2);
            addBicycleSafely(bike3);
            addBicycleSafely(bike4);
            addBicycleSafely(bike5);
            addBicycleSafely(bike6);
            addBicycleSafely(bike7);
            addBicycleSafely(bike8);

            System.out.println("Adding electric bicycles to inventory...");
            ElectricBicycle eBike1 = new ElectricBicycle(201, "E-Trek Pro", 10, 18.5, Color.RED, 500, 80, true);
            ElectricBicycle eBike2 = new ElectricBicycle(202, "E-Giant Urban", 8, 20.0, Color.BLACK, 400, 60, true);
            ElectricBicycle eBike3 = new ElectricBicycle(203, "E-Specialized", 12, 19.2, Color.WHITE, 600, 100, true);
            ElectricBicycle eBike4 = new ElectricBicycle(204, "E-Cannondale", 9, 17.8, Color.BLUE, 450, 70, true);
            ElectricBicycle eBike5 = new ElectricBicycle(205, "E-Scott Urban", 7, 19.5, Color.RED, 520, 85, true);

            addBicycleSafely(eBike1);
            addBicycleSafely(eBike2);
            addBicycleSafely(eBike3);
            addBicycleSafely(eBike4);
            addBicycleSafely(eBike5);

            displayInventorySummary();

        }

        catch (Exception e) {
            System.err.println("Error initializing bicycle inventory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addBicycleSafely(Bicycle bicycle) {
        try {
            rentalService.addBicycle(bicycle);
            String type = bicycle instanceof ElectricBicycle ? "E-Bike" : "Bike";
            System.out.println("Added " + type + ": " + bicycle.getBrand() + " (ID: " + bicycle.getId() + ", " + bicycle.getColor() + ")");
        }

        catch (Exception e) {
            System.out.println("Failed to add bicycle: " + bicycle.getBrand() + " - " + e.getMessage());
        }
    }

    private void displayInventorySummary() {
        System.out.println("\n Bicycle Rental Shop Inventory:");
        System.out.println("==================================");

        long regularBikes = rentalService.getAllBicycles().stream()
                .filter(bike -> !(bike instanceof ElectricBicycle))
                .count();

        long electricBikes = rentalService.getAllBicycles().stream()
                .filter(bike -> bike instanceof ElectricBicycle)
                .count();

        int totalBicycles = rentalService.getAllBicycles().size();
        int availableBicycles = rentalService.getAllAvailableBicycles().size();

        System.out.println("Regular Bicycles: " + regularBikes);
        System.out.println("Electric Bicycles: " + electricBikes);
        System.out.println("Total Inventory: " + totalBicycles + " bicycles");
        System.out.println("Available for Rent: " + availableBicycles + " bicycles");

        System.out.println("\n Available Bicycle Models:");
        rentalService.getAllBicycles().forEach(bicycle -> {
            String type = bicycle instanceof ElectricBicycle ? "[E-BIKE]" : "[BIKE]";
            String status = bicycle.isRented() ? "[RENTED]" : "[AVAILABLE]";
            System.out.println("   - ID: " + bicycle.getId() + " | " + bicycle.getBrand() +
                    " (" + bicycle.getColor() + ") " + type + " " + status);
        });

        System.out.println("==================================");
        System.out.println("Bicycle inventory ready!");
        System.out.println("Users can now register via JavaFX client");
        System.out.println("Server listening on: localhost:34567");
        System.out.println("Start the JavaFX client to begin renting!");
        System.out.println("==================================\n");
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            isRunning = true;
            System.out.println("Bicycle Rental Server started on port " + port);
            System.out.println("Waiting for client connections...");

            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket.getInetAddress());

                    HandleRequest requestHandler = new HandleRequest(clientSocket, controllers);
                    requestHandler.handleRequest();

                }

                catch (IOException e) {
                    if (isRunning) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }

        }

        catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }

        finally {
            cleanup();
        }
    }

    public void stop() {
        System.out.println("Stopping server...");
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
    }

    private void cleanup() {
        try {

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            System.out.println("Server stopped and resources cleaned up");
        }

        catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    public void addController(String name, Object controller) {
        controllers.put(name.toLowerCase(), controller);
        System.out.println("Controller added: " + name);
    }

    public int getPort() {
        return port;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public RentalService getRentalService() {
        return rentalService;
    }
}