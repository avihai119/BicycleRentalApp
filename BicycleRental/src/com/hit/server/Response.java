package com.hit.server;

import java.util.Map;

public class Response<T> {
    private Map<String, String> headers;
    private T body;
    private boolean success;
    private String message;

    public Response() {}

    public Response(Map<String, String> headers, T body) {
        this.headers = headers;
        this.body = body;
        this.success = true;
    }

    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Static factory method for success responses
    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.body = data;
        response.success = true;
        return response;
    }

    // Static factory method for error responses
    public static Response<String> error(String errorMessage) {
        Response<String> response = new Response<>();
        response.success = false;
        response.message = errorMessage;
        return response;
    }

    // Getters and setters
    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}