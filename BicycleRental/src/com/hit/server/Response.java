package com.hit.server;

import java.util.HashMap;
import java.util.Map;

public class Response<T> {
    private Map<String, String> headers;
    private T body;
    private String status;

    public Response() {
        this.headers = new HashMap<>();
    }

    public Response(T body) {
        this();
        this.body = body;
        this.status = "success";
    }

    public Response(String status, T body) {
        this();
        this.body = body;
        this.status = status;
    }

    public static <T> Response<T> success(T data) {
        return new Response<>("success", data);
    }

    public static <T> Response<T> error(String message) {
        return new Response<>("error", (T) message);
    }

    public Map<String, String> getHeaders() { return headers; }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }
    public T getBody() { return body; }
    public void setBody(T body) { this.body = body; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
