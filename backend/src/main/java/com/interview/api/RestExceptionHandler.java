package com.interview.api;

import com.interview.service.exception.CustomerNotFound;
import com.interview.service.exception.ServiceException;
import com.interview.service.exception.VehicleNotFound;
import com.interview.service.exception.WorkOrderNotFound;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleServiceException(ServiceException exception) {
        final HttpStatus status =
                switch (exception) {
                    case CustomerNotFound _, VehicleNotFound _, WorkOrderNotFound _ -> HttpStatus.NOT_FOUND;
                };
        LOGGER.warn("{}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return ResponseEntity.status(status).body(Map.of("status", status, "error", exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException exception) {
        final List<String> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        LOGGER.warn("Validation failed: {}", errors);
        return ResponseEntity.badRequest().body(Map.of("status", HttpStatus.BAD_REQUEST, "errors", errors));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleMessageNotReadable(HttpMessageNotReadableException exception) {
        LOGGER.warn("Malformed request body: {}", exception.getMessage());
        return ResponseEntity.badRequest()
                .body(Map.of("status", HttpStatus.BAD_REQUEST, "error", "Malformed request body"));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        LOGGER.warn("Data integrity violation: {}", exception.getMessage());
        // TODO message is pretty vague, could either implment deep exception translation or service validation
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("status", HttpStatus.CONFLICT, "error", "A conflicting record already exists"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception exception) {
        // Use a generic message to avoid leaking internal details (stack traces, class names, etc.)
        LOGGER.error("Unexpected error", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR, "error", "An unexpected error occurred"));
    }
}
