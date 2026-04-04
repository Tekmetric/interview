package com.interview.controller;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Handles exceptions thrown by controllers and provides graceful error responses.
 */
@ControllerAdvice
public class ErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    /**
     * Handles EntityNotFoundException and returns a custom error response.
     *
     * @param e the EntityNotFoundException
     * @return a ResponseEntity with error details
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException e) {
        logger.error("Entity not found: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Not Found");
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoHandlerFound(NoHandlerFoundException e) {
        logger.error("No handler found for the request: {} {}", e.getHttpMethod(), e.getRequestURL());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Not Found");
        error.put("message", "Endpoint not found: " + e.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}