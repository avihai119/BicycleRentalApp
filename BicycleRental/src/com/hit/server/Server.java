package com.hit.server;

import com.hit.controller.BicycleController;
import com.hit.controller.RentalController;
import com.hit.controller.UserController;
import com.hit.service.RentalService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private int port;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private Map<String, Object> controllers;
    private boolean isRunning = false;
    private RentalService rentalService;

    public Server(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(10); // Pool of 10 threads
        this.controllers = new HashMap<>();
        initializeServices();
        initializeControllers();
    }

    private void initializeServices() {
        // Initialize the rental service
        this.rentalService = new RentalService();
        System.out.println("Rental service initialized");
    }

    private void initializeControllers() {
        try {
            // Initialize bicycle controller
            BicycleController bicycleController = new BicycleController(rentalService);
            controllers.put("bicyclecontroller", bicycleController);
            System.out.println("Bicycle controller initialized");

            // Initialize rental controller
            RentalController rentalController = new RentalController(rentalService);
            controllers.put("rentalcontroller", rentalController);
            System.out.println("Rental controller initialized");

            // Initialize user controller
            UserController userController = new UserController(rentalService);
            controllers.put("usercontroller", userController);
            System.out.println("User controller initialized");

            System.out.println("All controllers initialized successfully");

        } catch (Exception e) {
            System.err.println("Error initializing controllers: " + e.getMessage());
            e.printStackTrace();
        }
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

                    // Handle each client request in a separate thread
                    HandleRequest requestHandler = new HandleRequest(clientSocket, controllers);
                    threadPool.execute(requestHandler);

                } catch (IOException e) {
                    if (isRunning) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        } finally {
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
            if (threadPool != null && !threadPool.isShutdown()) {
                threadPool.shutdown();
            }
            System.out.println("Server stopped and resources cleaned up");
        } catch (IOException e) {
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
