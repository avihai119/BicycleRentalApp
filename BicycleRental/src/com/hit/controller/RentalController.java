package com.hit.controller;

import com.hit.server.Request;
import com.hit.server.Response;
import com.hit.service.RentalService;
import com.hit.dm.Bicycle;
import com.hit.dm.Rental;
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
        } catch (Exception e) {
            return Response.error("Error processing rental request: " + e.getMessage());
        }
    }

    private Response<?> handleRentBicycle(JsonObject body) {  // Changed to Response<?>
        try {
            long bicycleId = body.get("bicycleId").getAsLong();
            long userId = body.get("userId").getAsLong();

            Bicycle rentedBicycle = rentalService.rentBicycle(bicycleId, userId);

            if (rentedBicycle != null) {
                return Response.success(rentedBicycle);
            } else {
                return Response.error("Bicycle not available or already rented");
            }
        } catch (IllegalArgumentException e) {
            return Response.error(e.getMessage());
        } catch (Exception e) {
            return Response.error("Invalid request data: " + e.getMessage());
        }
    }

    private Response<?> handleReturnBicycle(JsonObject body) {  // Changed to Response<?>
        try {
            long bicycleId = body.get("bicycleId").getAsLong();

            boolean success = rentalService.endRental(bicycleId);

            if (success) {
                return Response.success("Bicycle returned successfully");
            } else {
                return Response.error("Failed to return bicycle - not currently rented");
            }
        } catch (Exception e) {
            return Response.error("Invalid request data: " + e.getMessage());
        }
    }

    private Response<?> handleGetActiveRentals() {  // Changed to Response<?>
        try {
            List<Rental> activeRentals = rentalService.getActiveRentals();
            return Response.success(activeRentals);
        } catch (Exception e) {
            return Response.error("Error retrieving active rentals: " + e.getMessage());
        }
    }

    private Response<?> handleGetRentalHistory(JsonObject body) {  // Changed to Response<?>
        try {
            if (body.has("userId")) {
                long userId = body.get("userId").getAsLong();
                List<Rental> userRentals = rentalService.getUserRentalHistory(userId);
                return Response.success(userRentals);
            } else {
                List<Rental> allRentals = rentalService.getAllRentals();
                return Response.success(allRentals);
            }
        } catch (Exception e) {
            return Response.error("Error retrieving rental history: " + e.getMessage());
        }
    }

    private Response<?> handleSearchRental(JsonObject body) {  // Changed to Response<?>
        try {
            long rentalId = body.get("rentalId").getAsLong();

            Rental rental = rentalService.searchRental(rentalId);

            if (rental != null) {
                return Response.success(rental);
            } else {
                return Response.error("Rental not found");
            }
        } catch (Exception e) {
            return Response.error("Invalid request data: " + e.getMessage());
        }
    }
}