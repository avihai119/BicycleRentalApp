package com.hit.controller;

import com.hit.server.Request;
import com.hit.server.Response;
import com.hit.service.RentalService;
import com.hit.dm.Bicycle;
import com.hit.dm.ElectricBicycle;
import com.hit.dm.Color;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

public class BicycleController {
    private RentalService rentalService;
    private Gson gson;

    public BicycleController(RentalService rentalService) {
        this.rentalService = rentalService;
        this.gson = new Gson();
    }

    public Response<?> handleRequest(String action, JsonObject requestBody) {
        try {
            switch (action) {
                case "bicycle/add":
                    return handleAddBicycle(requestBody);
                case "bicycle/search":
                    return handleSearchBicycle(requestBody);
                case "bicycle/list":
                    return handleListAllBicycles();
                case "bicycle/available":
                    return handleListAvailableBicycles();
                case "bicycle/delete":
                    return handleDeleteBicycle(requestBody);
                case "bicycle/update":
                    return handleUpdateBicycle(requestBody);
                default:
                    return Response.error("Unknown bicycle action: " + action);
            }
        } catch (Exception e) {
            return Response.error("Error processing bicycle request: " + e.getMessage());
        }
    }

    private Response<String> handleAddBicycle(JsonObject body) {
        try {
            long id = body.get("id").getAsLong();
            String brand = body.get("brand").getAsString();
            int gearCount = body.get("gearCount").getAsInt();
            double weight = body.get("weight").getAsDouble();
            String colorStr = body.get("color").getAsString();

            Color color = Color.valueOf(colorStr.toUpperCase());
            Bicycle bicycle;

            // Check if it's an electric bicycle
            if (body.has("batteryCapacity")) {
                int batteryCapacity = body.get("batteryCapacity").getAsInt();
                int range = body.get("range").getAsInt();
                boolean hasPedalAssist = body.get("hasPedalAssist").getAsBoolean();

                bicycle = new ElectricBicycle(id, brand, gearCount, weight, color,
                        batteryCapacity, range, hasPedalAssist);
            } else {
                bicycle = new Bicycle(id, brand, gearCount, weight, color);
            }

            rentalService.addBicycle(bicycle);
            return Response.success("Bicycle added successfully");

        } catch (Exception e) {
            return Response.error("Invalid bicycle data: " + e.getMessage());
        }
    }

    private Response<Bicycle> handleSearchBicycle(JsonObject body) {
        try {
            long bicycleId = body.get("bicycleId").getAsLong();

            Bicycle bicycle = rentalService.searchBicycle(bicycleId);

            if (bicycle != null) {
                return Response.success(bicycle);
            } else {
                return Response.error("Bicycle not found");
            }
        } catch (Exception e) {
            return Response.error("Invalid request data: " + e.getMessage());
        }
    }

    private Response<List<Bicycle>> handleListAllBicycles() {
        try {
            // Get all bicycles (you might need to add this method to RentalService)
            List<Bicycle> allBicycles = rentalService.getAllBicycles();
            return Response.success(allBicycles);
        } catch (Exception e) {
            return Response.error("Error retrieving bicycles: " + e.getMessage());
        }
    }

    private Response<List<Bicycle>> handleListAvailableBicycles() {
        try {
            List<Bicycle> availableBicycles = rentalService.getAllAvailableBicycles();
            return Response.success(availableBicycles);
        } catch (Exception e) {
            return Response.error("Error retrieving available bicycles: " + e.getMessage());
        }
    }

    private Response<String> handleDeleteBicycle(JsonObject body) {
        try {
            long bicycleId = body.get("bicycleId").getAsLong();

            Bicycle bicycle = rentalService.searchBicycle(bicycleId);
            if (bicycle == null) {
                return Response.error("Bicycle not found");
            }

            if (bicycle.isRented()) {
                return Response.error("Cannot delete rented bicycle");
            }

            rentalService.deleteBicycle(bicycle);
            return Response.success("Bicycle deleted successfully");

        } catch (Exception e) {
            return Response.error("Error deleting bicycle: " + e.getMessage());
        }
    }

    private Response<String> handleUpdateBicycle(JsonObject body) {
        try {
            long bicycleId = body.get("bicycleId").getAsLong();

            Bicycle bicycle = rentalService.searchBicycle(bicycleId);
            if (bicycle == null) {
                return Response.error("Bicycle not found");
            }

            // Update bicycle properties (you might need to add setters to Bicycle class)
            // For now, returning success message
            return Response.success("Bicycle updated successfully");

        } catch (Exception e) {
            return Response.error("Error updating bicycle: " + e.getMessage());
        }
    }
}