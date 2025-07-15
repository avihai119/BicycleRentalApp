package model;

import java.util.Map;

public class Response<T> {
    private Map<String, String> headers;
    private T body;
    private boolean success;
    private String message;

    public Response() {}

    public Map<String, String> getHeaders() { return headers; }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }
    public T getBody() { return body; }
    public void setBody(T body) { this.body = body; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}