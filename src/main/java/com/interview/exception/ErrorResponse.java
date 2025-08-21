package com.interview.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
    String type,
    String title,
    int status,
    String detail,
    String instance,
    LocalDateTime timestamp,
    List<ValidationError> errors
) {
    public ErrorResponse(String type, String title, int status, String detail, String instance) {
        this(type, title, status, detail, instance, LocalDateTime.now(), null);
    }
    
    public ErrorResponse(String type, String title, int status, String detail, String instance, List<ValidationError> errors) {
        this(type, title, status, detail, instance, LocalDateTime.now(), errors);
    }
    
    public record ValidationError(String field, String message, Object rejectedValue) {}
}