package com.hit.server;

import com.hit.service.RentalService;
import com.hit.dm.*;

public class ServerDriver {

    public static void main(String[] args) {
        // Create the server instance
        Server server = new Server(34567);

        // Start the server on a separate thread using lambda expression
        Thread serverThread = new Thread(() -> server.start());
        serverThread.start();

        // Add shutdown hook for graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            server.stop();
            try {
                serverThread.join(5000); // Wait up to 5 seconds for server to stop
            } catch (InterruptedException e) {
                System.err.println("Interrupted while waiting for server to stop");
                Thread.currentThread().interrupt();
            }
        }));

        System.out.println("Server started on port 34567. Press Ctrl+C to stop.");
    }

    private static void initializeBicycles(RentalService rentalService) {
        System.out.println("🚀 Initializing bicycle inventory...");

        try {
            // Add regular bicycles
            System.out.println("🚲 Adding regular bicycles to inventory...");
            Bicycle bike1 = new Bicycle(101, "Trek Mountain Bike", 21, 12.5, Color.BLUE);
            Bicycle bike2 = new Bicycle(102, "Giant Road Bike", 18, 11.0, Color.RED);
            Bicycle bike3 = new Bicycle(103, "Specialized Hybrid", 24, 13.2, Color.BLACK);
            Bicycle bike4 = new Bicycle(104, "Cannondale Trail", 27, 14.0, Color.WHITE);
            Bicycle bike5 = new Bicycle(105, "Scott City Bike", 7, 10.5, Color.BLUE);
            Bicycle bike6 = new Bicycle(106, "GT Mountain", 21, 13.8, Color.RED);
            Bicycle bike7 = new Bicycle(107, "Merida Road", 16, 9.8, Color.BLACK);
            Bicycle bike8 = new Bicycle(108, "Cube Hybrid", 24, 12.0, Color.WHITE);

            addBicycleSafely(rentalService, bike1);
            addBicycleSafely(rentalService, bike2);
            addBicycleSafely(rentalService, bike3);
            addBicycleSafely(rentalService, bike4);
            addBicycleSafely(rentalService, bike5);
            addBicycleSafely(rentalService, bike6);
            addBicycleSafely(rentalService, bike7);
            addBicycleSafely(rentalService, bike8);

            // Add electric bicycles
            System.out.println("⚡ Adding electric bicycles to inventory...");
            ElectricBicycle eBike1 = new ElectricBicycle(201, "E-Trek Pro", 10, 18.5, Color.RED, 500, 80, true);
            ElectricBicycle eBike2 = new ElectricBicycle(202, "E-Giant Urban", 8, 20.0, Color.BLACK, 400, 60, true);
            ElectricBicycle eBike3 = new ElectricBicycle(203, "E-Specialized", 12, 19.2, Color.WHITE, 600, 100, true);
            ElectricBicycle eBike4 = new ElectricBicycle(204, "E-Cannondale", 9, 17.8, Color.BLUE, 450, 70, true);
            ElectricBicycle eBike5 = new ElectricBicycle(205, "E-Scott Urban", 7, 19.5, Color.RED, 520, 85, true);

            addBicycleSafely(rentalService, eBike1);
            addBicycleSafely(rentalService, eBike2);
            addBicycleSafely(rentalService, eBike3);
            addBicycleSafely(rentalService, eBike4);
            addBicycleSafely(rentalService, eBike5);

            // Display inventory summary
            displayInventorySummary(rentalService);

        } catch (Exception e) {
            System.err.println("❌ Error initializing bicycle inventory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void addBicycleSafely(RentalService rentalService, Bicycle bicycle) {
        try {
            rentalService.addBicycle(bicycle);
            String type = bicycle instanceof ElectricBicycle ? "E-Bike" : "Bike";
            System.out.println("  ✅ Added " + type + ": " + bicycle.getBrand() + " (ID: " + bicycle.getId() + ", " + bicycle.getColor() + ")");
        } catch (Exception e) {
            System.out.println("  ❌ Failed to add bicycle: " + bicycle.getBrand() + " - " + e.getMessage());
        }
    }

    private static void displayInventorySummary(RentalService rentalService) {
        System.out.println("\n📊 Bicycle Rental Shop Inventory:");
        System.out.println("==================================");

        // Count bicycles by type
        long regularBikes = rentalService.getAllBicycles().stream()
                .filter(bike -> !(bike instanceof ElectricBicycle))
                .count();

        long electricBikes = rentalService.getAllBicycles().stream()
                .filter(bike -> bike instanceof ElectricBicycle)
                .count();

        int totalBicycles = rentalService.getAllBicycles().size();
        int availableBicycles = rentalService.getAllAvailableBicycles().size();

        System.out.println("🚲 Regular Bicycles: " + regularBikes);
        System.out.println("⚡ Electric Bicycles: " + electricBikes);
        System.out.println("📈 Total Inventory: " + totalBicycles + " bicycles");
        System.out.println("✅ Available for Rent: " + availableBicycles + " bicycles");

        System.out.println("\n🏪 Available Bicycle Models:");
        rentalService.getAllBicycles().forEach(bicycle -> {
            String type = bicycle instanceof ElectricBicycle ? "[E-BIKE]" : "[BIKE]";
            String status = bicycle.isRented() ? "[RENTED]" : "[AVAILABLE]";
            System.out.println("   - ID: " + bicycle.getId() + " | " + bicycle.getBrand() +
                    " (" + bicycle.getColor() + ") " + type + " " + status);
        });

        System.out.println("==================================");
        System.out.println("✅ Bicycle inventory ready!");
        System.out.println("👥 Users can now register via JavaFX client");
        System.out.println("🔗 Server listening on: localhost:34567");
        System.out.println("📱 Start the JavaFX client to begin renting!");
        System.out.println("==================================\n");
    }
}