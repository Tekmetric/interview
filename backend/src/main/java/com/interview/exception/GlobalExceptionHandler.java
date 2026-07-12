package com.interview.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        log.warn("api_error code={} status={} path={} message={}",
                "not_found",
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI(),
                ex.getMessage()
        );
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
        log.warn("api_error code={} status={} path={} message={}",
                "conflict",
                HttpStatus.CONFLICT.value(),
                request.getRequestURI(),
                ex.getMessage()
        );
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

        log.warn("api_error code={} status={} path={} validationErrors={}",
                "validation_error",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                validationErrors
        );
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

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorDto> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("api_error code={} status={} path={} message={}",
                "forbidden",
                HttpStatus.FORBIDDEN.value(),
                request.getRequestURI(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new ApiErrorDto(
                        "forbidden",
                        "Access denied",
                        HttpStatus.FORBIDDEN.value(),
                        request.getRequestURI(),
                        Instant.now(),
                        null
                )
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorDto> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ApiErrorDto(
                        "unauthorized",
                        "Authentication required",
                        HttpStatus.UNAUTHORIZED.value(),
                        request.getRequestURI(),
                        Instant.now(),
                        null
                )
        );
    }

    /**
     * Fallback for any uncaught exception so all error responses use the same ApiError shape.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleAny(Exception ex, HttpServletRequest request) {
        log.error("api_error code={} status={} path={} message={}",
                "internal_error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI(),
                ex.getMessage(),
                ex
        );
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
