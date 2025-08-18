package com.interview.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message;
        if (ex.getRequiredType() != null && ex.getRequiredType().equals(UUID.class)) {
            message = "Invalid UUID format: " + ex.getValue();
        } else {
            message = "Invalid parameter: " + ex.getName();
        }
        
        Map<String, Object> errorBody = Map.of(
            "status", 400,
            "error", "Bad Request",
            "message", message
        );
        
        return ResponseEntity.badRequest().body(errorBody);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, Object> errorBody = Map.of(
                "status", ex.getStatusCode().value(),
                "error", ((HttpStatus) ex.getStatusCode()).getReasonPhrase(),
                "message", Optional.ofNullable(ex.getReason()).orElse("No reason provided")
        );

        return ResponseEntity.status(ex.getStatusCode()).body(errorBody);
    }
}