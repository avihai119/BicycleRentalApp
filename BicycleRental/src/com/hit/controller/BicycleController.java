package com.hit.controller;

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
        }

        catch (Exception e) {
            return Response.error("Error processing bicycle request: " + e.getMessage());
        }
    }

    private Response<?> handleAddBicycle(JsonObject body) {
        try {
            long id = body.get("id").getAsLong();
            String brand = body.get("brand").getAsString();
            int gearCount = body.get("gearCount").getAsInt();
            double weight = body.get("weight").getAsDouble();
            String colorStr = body.get("color").getAsString();

            Color color = Color.valueOf(colorStr.toUpperCase());
            Bicycle bicycle;

            if (body.has("batteryCapacity")) {
                int batteryCapacity = body.get("batteryCapacity").getAsInt();
                int range = body.get("range").getAsInt();
                boolean hasPedalAssist = body.get("hasPedalAssist").getAsBoolean();

                bicycle = new ElectricBicycle(id, brand, gearCount, weight, color,
                        batteryCapacity, range, hasPedalAssist);
            }

            else {
                bicycle = new Bicycle(id, brand, gearCount, weight, color);
            }

            rentalService.addBicycle(bicycle);
            return Response.success("Bicycle added successfully");

        }

        catch (Exception e) {
            return Response.error("Invalid bicycle data: " + e.getMessage());
        }
    }

    private Response<?> handleSearchBicycle(JsonObject body) {  // Changed to Response<?>
        try {
            long bicycleId = body.get("bicycleId").getAsLong();

            Bicycle bicycle = rentalService.searchBicycle(bicycleId);

            if (bicycle != null) {
                return Response.success(bicycle);
            }

            else {
                return Response.error("Bicycle not found");
            }
        }

        catch (Exception e) {
            return Response.error("Invalid request data: " + e.getMessage());
        }
    }

    private Response<?> handleListAllBicycles() {
        try {
            List<Bicycle> allBicycles = rentalService.getAllBicycles();
            return Response.success(allBicycles);
        }

        catch (Exception e) {
            return Response.error("Error retrieving bicycles: " + e.getMessage());
        }
    }

    private Response<?> handleListAvailableBicycles() {
        try {
            List<Bicycle> availableBicycles = rentalService.getAllAvailableBicycles();
            return Response.success(availableBicycles);
        }

        catch (Exception e) {
            return Response.error("Error retrieving available bicycles: " + e.getMessage());
        }
    }

    private Response<?> handleDeleteBicycle(JsonObject body) {
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
        }

        catch (Exception e) {
            return Response.error("Error deleting bicycle: " + e.getMessage());
        }
    }

    private Response<?> handleUpdateBicycle(JsonObject body) {  // Changed to Response<?>
        try {
            long bicycleId = body.get("bicycleId").getAsLong();

            Bicycle bicycle = rentalService.searchBicycle(bicycleId);

            if (bicycle == null) {
                return Response.error("Bicycle not found");
            }

            return Response.success("Bicycle updated successfully");

        }

        catch (Exception e) {
            return Response.error("Error updating bicycle: " + e.getMessage());
        }
    }
}