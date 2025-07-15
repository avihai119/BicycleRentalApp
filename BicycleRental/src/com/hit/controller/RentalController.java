package com.hit.controller;

import com.hit.server.Response;
import com.hit.service.RentalService;
import com.hit.dm.Bicycle;
import com.hit.dm.Rental;
import com.hit.dm.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

public class RentalController {
    private RentalService rentalService;
    private Gson gson;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
        this.gson = new Gson();
    }

    public Response<?> handleRequest(String action, JsonObject requestBody) {
        try {
            switch (action) {
                case "rental/rent":
                    return handleRentBicycle(requestBody);
                case "rental/return":
                    return handleReturnBicycle(requestBody);
                case "rental/active":
                    return handleGetActiveRentals();
                case "rental/history":
                    return handleGetRentalHistory(requestBody);
                case "rental/search":
                    return handleSearchRental(requestBody);
                default:
                    return Response.error("Unknown rental action: " + action);
            }
        }

        catch (Exception e) {
            return Response.error("Error processing rental request: " + e.getMessage());
        }
    }

    private Response<?> handleRentBicycle(JsonObject body) {
        try {
            long bicycleId = body.get("bicycleId").getAsLong();
            long userId = body.get("userId").getAsLong();

            System.out.println("Processing rental request - User: " + userId + ", Bicycle: " + bicycleId);

            User user = rentalService.searchUser(userId);
            if (user == null) {
                String defaultName = "User " + userId;
                int defaultAge = 25;
                user = new User(userId, defaultName, defaultAge);

                try {
                    rentalService.addUser(user);
                    System.out.println("Auto-created user: " + defaultName + " (ID: " + userId + ")");
                }

                catch (Exception e) {
                    System.err.println("Failed to create user: " + e.getMessage());
                    return Response.error("Failed to create user: " + e.getMessage());
                }
            }

            else {
                System.out.println("Found existing user: " + user.getName() + " (ID: " + userId + ")");
            }

            Bicycle rentedBicycle = rentalService.rentBicycle(bicycleId, userId);

            if (rentedBicycle != null) {
                System.out.println("Bicycle rented successfully - ID: " + bicycleId + " to User: " + userId);
                return Response.success(rentedBicycle);
            }

            else {
                System.out.println("Rental failed - Bicycle: " + bicycleId + " not available");
                return Response.error("Bicycle not available or already rented");
            }
        }

        catch (IllegalArgumentException e) {
            System.err.println("Rental error: " + e.getMessage());
            return Response.error(e.getMessage());
        }

        catch (Exception e) {
            System.err.println("Unexpected rental error: " + e.getMessage());
            e.printStackTrace();
            return Response.error("Invalid request data: " + e.getMessage());
        }
    }

    private Response<?> handleReturnBicycle(JsonObject body) {
        try {
            long bicycleId = body.get("bicycleId").getAsLong();

            System.out.println("Processing return request - Bicycle: " + bicycleId);

            boolean success = rentalService.endRental(bicycleId);

            if (success) {
                System.out.println("Bicycle returned successfully - ID: " + bicycleId);
                return Response.success("Bicycle returned successfully");
            }

            else {
                System.out.println("Return failed - Bicycle: " + bicycleId + " not currently rented");
                return Response.error("Failed to return bicycle - not currently rented");
            }
        }

        catch (Exception e) {
            System.err.println("Return error: " + e.getMessage());
            return Response.error("Invalid request data: " + e.getMessage());
        }
    }

    private Response<?> handleGetActiveRentals() {
        try {
            List<Rental> activeRentals = rentalService.getActiveRentals();
            System.out.println("Retrieved " + activeRentals.size() + " active rentals");
            return Response.success(activeRentals);
        }

        catch (Exception e) {
            System.err.println("Error retrieving active rentals: " + e.getMessage());
            return Response.error("Error retrieving active rentals: " + e.getMessage());
        }
    }

    private Response<?> handleGetRentalHistory(JsonObject body) {
        try {
            if (body.has("userId")) {
                long userId = body.get("userId").getAsLong();
                List<Rental> userRentals = rentalService.getUserRentalHistory(userId);
                System.out.println("Retrieved rental history for user " + userId + ": " + userRentals.size() + " rentals");
                return Response.success(userRentals);
            }

            else {
                List<Rental> allRentals = rentalService.getAllRentals();
                System.out.println("Retrieved all rental history: " + allRentals.size() + " rentals");
                return Response.success(allRentals);
            }
        }

        catch (Exception e) {
            System.err.println("Error retrieving rental history: " + e.getMessage());
            return Response.error("Error retrieving rental history: " + e.getMessage());
        }
    }

    private Response<?> handleSearchRental(JsonObject body) {
        try {
            long rentalId = body.get("rentalId").getAsLong();

            Rental rental = rentalService.searchRental(rentalId);

            if (rental != null) {
                System.out.println("Found rental ID: " + rentalId);
                return Response.success(rental);
            }

            else {
                System.out.println("Rental not found - ID: " + rentalId);
                return Response.error("Rental not found");
            }
        }

        catch (Exception e) {
            System.err.println("Search rental error: " + e.getMessage());
            return Response.error("Invalid request data: " + e.getMessage());
        }
    }
}