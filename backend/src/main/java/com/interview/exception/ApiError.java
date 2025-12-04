package com.interview.exception;

import java.time.Instant;
import java.util.List;

public class ApiError {
    private Instant timestamp = Instant.now();
    private int status;
    private String error;
    private String message;
    private List<String> errors;

    // constructors / getters / setters
    public ApiError() {}
    public ApiError(int status, String error, String message, List<String> errors) {
        this.status = status; this.error = error; this.message = message; this.errors = errors;
    }
    // getters/setters...
    public Instant getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public List<String> getErrors() { return errors; }
}