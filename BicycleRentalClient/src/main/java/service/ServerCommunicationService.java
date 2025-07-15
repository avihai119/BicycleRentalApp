package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Request;
import model.Response;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ServerCommunicationService {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 34567;
    private Gson gson;

    public ServerCommunicationService() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .serializeNulls()
                .create();
    }

    public <T> Response<T> sendRequest(String action, Object requestBody, Type responseType) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            Map<String, String> headers = new HashMap<>();
            headers.put("action", action);

            Object body = requestBody != null ? requestBody : new HashMap<String, Object>();
            Request<Object> request = new Request<>(headers, body);

            String jsonRequest = gson.toJson(request);
            System.out.println("Sending request: " + jsonRequest);
            writer.println(jsonRequest);

            String jsonResponse = reader.readLine();
            System.out.println("Received response: " + jsonResponse);

            if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
                return createErrorResponse("Empty response from server");
            }

            Response<T> response = gson.fromJson(jsonResponse, responseType);
            return response;

        }

        catch (IOException e) {
            System.err.println("Communication error: " + e.getMessage());
            return createErrorResponse("Failed to connect to server: " + e.getMessage());
        }

        catch (Exception e) {
            System.err.println("Error processing request: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse("Error processing request: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Response<T> createErrorResponse(String errorMessage) {
        Response<T> response = new Response<>();
        response.setSuccess(false);
        response.setMessage(errorMessage);
        return response;
    }

    // Helper methods for common request types
    public Response<String> sendStringRequest(String action, Object requestBody) {
        Type responseType = new TypeToken<Response<String>>(){}.getType();
        return sendRequest(action, requestBody, responseType);
    }

    public <T> Response<T> sendObjectRequest(String action, Object requestBody, Class<T> responseClass) {
        Type responseType = TypeToken.getParameterized(Response.class, responseClass).getType();
        return sendRequest(action, requestBody, responseType);
    }

    public <T> Response<java.util.List<T>> sendListRequest(String action, Object requestBody, Class<T> listItemClass) {
        Type listType = TypeToken.getParameterized(java.util.List.class, listItemClass).getType();
        Type responseType = TypeToken.getParameterized(Response.class, listType).getType();
        return sendRequest(action, requestBody, responseType);
    }
}