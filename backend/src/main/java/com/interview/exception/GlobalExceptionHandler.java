package com.interview.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiErrorDto(
                        "not_found",
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND.value(),
                        request.getRequestURI(),
                        Instant.now(),
                        null
                )
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorDto> handleConflict(ConflictException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ApiErrorDto(
                        "conflict",
                        ex.getMessage(),
                        HttpStatus.CONFLICT.value(),
                        request.getRequestURI(),
                        Instant.now(),
                        null
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (a, b) -> a
                ));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiErrorDto(
                        "validation_error",
                        "Request validation failed",
                        HttpStatus.BAD_REQUEST.value(),
                        request.getRequestURI(),
                        Instant.now(),
                        validationErrors
                )
        );
    }

    /**
     * Fallback for any uncaught exception so all error responses use the same ApiError shape.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleAny(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiErrorDto(
                        "internal_error",
                        "An unexpected error occurred",
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        request.getRequestURI(),
                        Instant.now(),
                        null
                )
        );
    }
}
