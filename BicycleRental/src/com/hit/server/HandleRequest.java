package com.hit.server;

import com.hit.controller.BicycleController;
import com.hit.controller.RentalController;
import com.hit.controller.UserController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hit.dao.LocalDateTimeAdapter;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Scanner;

public class HandleRequest {
    private Socket clientSocket;
    private Map<String, Object> controllers;
    private Gson gson;

    public HandleRequest(Socket clientSocket, Map<String, Object> controllers) {
        this.clientSocket = clientSocket;
        this.controllers = controllers;
        // Configure Gson properly to match client
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .serializeNulls()
                .create();
    }

    public void handleRequest() {
        PrintWriter writer = null;
        Scanner reader = null;

        try {
            reader = new Scanner(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);

            System.out.println("Processing client request...");

            // Read the complete JSON request using Scanner
            String requestStr = "";
            if (reader.hasNextLine()) {
                requestStr = reader.nextLine().trim();
            }

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

                // Make sure the response is sent completely
                writer.println(jsonResponse);
                writer.flush();

                // Small delay to ensure data is sent
                Thread.sleep(10);

            } catch (Exception e) {
                System.err.println("Error processing request: " + e.getMessage());
                e.printStackTrace();
                sendErrorResponse(writer, "Error processing request: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Error handling client request: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up resources
            try {
                if (writer != null) {
                    writer.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
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
            // Ensure requestBody is never null
            JsonObject body = requestBody != null ? requestBody : new JsonObject();

            switch (controllerName) {
                case "bicycle":
                    BicycleController bicycleController = (BicycleController) controllers.get("bicyclecontroller");
                    if (bicycleController == null) {
                        return createErrorResponse("Bicycle controller not found");
                    }
                    return bicycleController.handleRequest(action, body);

                case "rental":
                    RentalController rentalController = (RentalController) controllers.get("rentalcontroller");
                    if (rentalController == null) {
                        return createErrorResponse("Rental controller not found");
                    }
                    return rentalController.handleRequest(action, body);

                case "user":
                    UserController userController = (UserController) controllers.get("usercontroller");
                    if (userController == null) {
                        return createErrorResponse("User controller not found");
                    }
                    return userController.handleRequest(action, body);

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
        try {
            Response<String> errorResponse = createErrorResponse(errorMessage);
            String jsonResponse = gson.toJson(errorResponse);
            System.out.println("Sending error response: " + jsonResponse);
            writer.println(jsonResponse);
            writer.flush();
            Thread.sleep(10); // Ensure data is sent
        } catch (Exception e) {
            System.err.println("Error sending error response: " + e.getMessage());
        }
    }
}