package com.hit.server;

import com.hit.controller.BicycleController;
import com.hit.controller.RentalController;
import com.hit.controller.UserController;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

public class HandleRequest implements Runnable {
    private Socket clientSocket;
    private Map<String, Object> controllers;
    private Gson gson;

    public HandleRequest(Socket clientSocket, Map<String, Object> controllers) {
        this.clientSocket = clientSocket;
        this.controllers = controllers;
        this.gson = new Gson();
    }

    @Override
    public void run() {
        try (Scanner reader = new Scanner(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true)) {

            System.out.println("Processing client request...");

            // Read the JSON request from client
            StringBuilder jsonRequest = new StringBuilder();
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                jsonRequest.append(line);
                // Check if we have a complete JSON object
                if (line.trim().equals("}") && jsonRequest.toString().trim().startsWith("{")) {
                    break;
                }
            }

            String requestStr = jsonRequest.toString().trim();
            System.out.println("Received request: " + requestStr);

            if (requestStr.isEmpty()) {
                sendErrorResponse(writer, "Empty request received");
                return;
            }

            try {
                // Parse the request
                Type requestType = new TypeToken<Request<JsonObject>>(){}.getType();
                Request<JsonObject> request = gson.fromJson(requestStr, requestType);

                if (request == null || request.getHeaders() == null) {
                    sendErrorResponse(writer, "Invalid request format");
                    return;
                }

                String action = request.getAction();
                if (action == null) {
                    sendErrorResponse(writer, "No action specified in request");
                    return;
                }

                System.out.println("Processing action: " + action);

                // Route the request to appropriate controller
                Response<?> response = routeRequest(action, request.getBody());

                // Send response back to client
                String jsonResponse = gson.toJson(response);
                System.out.println("Sending response: " + jsonResponse);
                writer.println(jsonResponse);
                writer.flush();

            } catch (Exception e) {
                System.err.println("Error processing request: " + e.getMessage());
                e.printStackTrace();
                sendErrorResponse(writer, "Error processing request: " + e.getMessage());
            }

        } catch (IOException e) {
            System.err.println("Error handling client request: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private Response<?> routeRequest(String action, JsonObject requestBody) {
        String[] actionParts = action.split("/");

        if (actionParts.length < 2) {
            return createErrorResponse("Invalid action format. Expected: controller/method");
        }

        String controllerName = actionParts[0].toLowerCase();

        try {
            switch (controllerName) {
                case "bicycle":
                    BicycleController bicycleController = (BicycleController) controllers.get("bicyclecontroller");
                    if (bicycleController == null) {
                        return createErrorResponse("Bicycle controller not found");
                    }
                    return bicycleController.handleRequest(action, requestBody != null ? requestBody : new JsonObject());

                case "rental":
                    RentalController rentalController = (RentalController) controllers.get("rentalcontroller");
                    if (rentalController == null) {
                        return createErrorResponse("Rental controller not found");
                    }
                    return rentalController.handleRequest(action, requestBody != null ? requestBody : new JsonObject());

                case "user":
                    UserController userController = (UserController) controllers.get("usercontroller");
                    if (userController == null) {
                        return createErrorResponse("User controller not found");
                    }
                    return userController.handleRequest(action, requestBody != null ? requestBody : new JsonObject());

                default:
                    return createErrorResponse("Unknown controller: " + controllerName);
            }

        } catch (Exception e) {
            System.err.println("Error routing request: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse("Error processing request: " + e.getMessage());
        }
    }

    private Response<String> createErrorResponse(String errorMessage) {
        return Response.error(errorMessage);
    }

    private void sendErrorResponse(PrintWriter writer, String errorMessage) {
        Response<String> errorResponse = createErrorResponse(errorMessage);
        String jsonResponse = gson.toJson(errorResponse);
        writer.println(jsonResponse);
        writer.flush();
    }
}