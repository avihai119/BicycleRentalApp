package com.hit.controller;

import com.hit.dm.Rental;
import com.hit.server.Request;
import com.hit.server.Response;
import com.hit.service.RentalService;
import com.hit.dm.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

public class UserController {
    private RentalService rentalService;
    private Gson gson;

    public UserController(RentalService rentalService) {
        this.rentalService = rentalService;
        this.gson = new Gson();
    }

    public Response<?> handleRequest(String action, JsonObject requestBody) {
        try {
            switch (action) {
                case "user/add":
                    return handleAddUser(requestBody);
                case "user/search":
                    return handleSearchUser(requestBody);
                case "user/list":
                    return handleListAllUsers();
                case "user/update":
                    return handleUpdateUser(requestBody);
                case "user/delete":
                    return handleDeleteUser(requestBody);
                case "user/rentals":
                    return handleGetUserRentals(requestBody);
                default:
                    return Response.error("Unknown user action: " + action);
            }
        } catch (Exception e) {
            return Response.error("Error processing user request: " + e.getMessage());
        }
    }

    private Response<String> handleAddUser(JsonObject body) {
        try {
            long id = body.get("id").getAsLong();
            String name = body.get("name").getAsString();
            int age = body.get("age").getAsInt();

            User user = new User(id, name, age);
            rentalService.addUser(user);
            return Response.success("User added successfully");

        } catch (IllegalStateException e) {
            return Response.error(e.getMessage());
        } catch (Exception e) {
            return Response.error("Invalid user data: " + e.getMessage());
        }
    }

    private Response<User> handleSearchUser(JsonObject body) {
        try {
            long userId = body.get("userId").getAsLong();

            User user = rentalService.searchUser(userId);

            if (user != null) {
                return Response.success(user);
            } else {
                return Response.error("User not found");
            }
        } catch (Exception e) {
            return Response.error("Invalid request data: " + e.getMessage());
        }
    }

    private Response<List<User>> handleListAllUsers() {
        try {
            List<User> allUsers = rentalService.getAllUsers();
            return Response.success(allUsers);
        } catch (Exception e) {
            return Response.error("Error retrieving users: " + e.getMessage());
        }
    }

    private Response<String> handleUpdateUser(JsonObject body) {
        try {
            long userId = body.get("userId").getAsLong();

            User user = rentalService.searchUser(userId);
            if (user == null) {
                return Response.error("User not found");
            }

            // Update user properties
            if (body.has("name")) {
                String newName = body.get("name").getAsString();
                user.setName(newName);
            }

            if (body.has("age")) {
                int newAge = body.get("age").getAsInt();
                user.setAge(newAge);
            }

            rentalService.updateUser(user);
            return Response.success("User updated successfully");

        } catch (Exception e) {
            return Response.error("Error updating user: " + e.getMessage());
        }
    }

    private Response<String> handleDeleteUser(JsonObject body) {
        try {
            long userId = body.get("userId").getAsLong();

            User user = rentalService.searchUser(userId);
            if (user == null) {
                return Response.error("User not found");
            }

            // Check if user has active rentals
            if (rentalService.hasActiveRentals(userId)) {
                return Response.error("Cannot delete user with active rentals");
            }

            rentalService.deleteUser(user);
            return Response.success("User deleted successfully");

        } catch (Exception e) {
            return Response.error("Error deleting user: " + e.getMessage());
        }
    }

    private Response<List<Rental>> handleGetUserRentals(JsonObject body) {
        try {
            long userId = body.get("userId").getAsLong();

            User user = rentalService.searchUser(userId);
            if (user == null) {
                return Response.error("User not found");
            }

            List<Rental> userRentals = rentalService.getUserRentalHistory(userId);
            return Response.success(userRentals);

        } catch (Exception e) {
            return Response.error("Error retrieving user rentals: " + e.getMessage());
        }
    }
}