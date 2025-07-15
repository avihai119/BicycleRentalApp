package com.hit.server;

import com.hit.controller.BicycleController;
import com.hit.controller.RentalController;
import com.hit.controller.UserController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.hit.dao.LocalDateTimeAdapter;

import java.io.*;
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
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);

            System.out.println("🔗 Processing client request...");

            // Read the complete JSON request - handle potential multi-line JSON
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            int braceCount = 0;
            boolean inString = false;
            boolean escapeNext = false;

            // Read character by character to properly handle JSON
            int ch;
            while ((ch = reader.read()) != -1) {
                char c = (char) ch;
                jsonBuilder.append(c);

                if (escapeNext) {
                    escapeNext = false;
                    continue;
                }

                if (c == '\\' && inString) {
                    escapeNext = true;
                    continue;
                }

                if (c == '"' && !escapeNext) {
                    inString = !inString;
                    continue;
                }

                if (!inString) {
                    if (c == '{') {
                        braceCount++;
                    } else if (c == '}') {
                        braceCount--;
                        if (braceCount == 0) {
                            break; // Complete JSON object received
                        }
                    }
                }
            }

            String requestStr = jsonBuilder.toString().trim();

            if (requestStr.isEmpty()) {
                System.out.println("❌ Empty request received");
                sendErrorResponse(writer, "Empty request received");
                return;
            }

            System.out.println("📨 Received request: " + requestStr);

            try {
                // Parse the request JSON step by step
                JsonObject requestJson;
                try {
                    requestJson = JsonParser.parseString(requestStr).getAsJsonObject();
                } catch (JsonSyntaxException e) {
                    System.err.println("❌ Invalid JSON syntax: " + e.getMessage());
                    sendErrorResponse(writer, "Invalid JSON format");
                    return;
                }

                // Extract headers safely
                JsonObject headers = new JsonObject();
                if (requestJson.has("headers") && requestJson.get("headers").isJsonObject()) {
                    headers = requestJson.getAsJsonObject("headers");
                }

                // Extract body safely - always create a valid JsonObject
                JsonObject body = new JsonObject();
                if (requestJson.has("body")) {
                    if (requestJson.get("body").isJsonObject()) {
                        body = requestJson.getAsJsonObject("body");
                    } else if (requestJson.get("body").isJsonNull()) {
                        body = new JsonObject(); // Empty object for null body
                    }
                }

                // Get action from headers
                String action = null;
                if (headers.has("action")) {
                    action = headers.get("action").getAsString();
                }

                if (action == null || action.isEmpty()) {
                    System.out.println("❌ No action specified in request");
                    sendErrorResponse(writer, "No action specified in request");
                    return;
                }

                System.out.println("🚀 Processing action: " + action);

                // Route the request to appropriate controller
                Response<?> response = routeRequest(action, body);

                // Send response back to client
                String jsonResponse = gson.toJson(response);
                System.out.println("📤 Sending response: " + jsonResponse);

                writer.println(jsonResponse);
                writer.flush();

            } catch (Exception e) {
                System.err.println("❌ Error processing request: " + e.getMessage());
                e.printStackTrace();
                sendErrorResponse(writer, "Error processing request: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("❌ Error handling client request: " + e.getMessage());
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
                System.err.println("❌ Error closing client socket: " + e.getMessage());
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
                        System.err.println("❌ Bicycle controller not found");
                        return createErrorResponse("Bicycle controller not found");
                    }
                    System.out.println("🚲 Routing to bicycle controller");
                    return bicycleController.handleRequest(action, body);

                case "rental":
                    RentalController rentalController = (RentalController) controllers.get("rentalcontroller");
                    if (rentalController == null) {
                        System.err.println("❌ Rental controller not found");
                        return createErrorResponse("Rental controller not found");
                    }
                    System.out.println("🏠 Routing to rental controller");
                    return rentalController.handleRequest(action, body);

                case "user":
                    UserController userController = (UserController) controllers.get("usercontroller");
                    if (userController == null) {
                        System.err.println("❌ User controller not found");
                        return createErrorResponse("User controller not found");
                    }
                    System.out.println("👤 Routing to user controller");
                    return userController.handleRequest(action, body);

                default:
                    System.err.println("❌ Unknown controller: " + controllerName);
                    return createErrorResponse("Unknown controller: " + controllerName);
            }

        } catch (Exception e) {
            System.err.println("❌ Error routing request: " + e.getMessage());
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
            System.out.println("📤 Sending error response: " + jsonResponse);
            writer.println(jsonResponse);
            writer.flush();
        } catch (Exception e) {
            System.err.println("❌ Error sending error response: " + e.getMessage());
        }
    }
}