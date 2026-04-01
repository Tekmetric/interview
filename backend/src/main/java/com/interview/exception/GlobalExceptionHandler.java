
package com.interview.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // For client-side errors (4XX), we just log a warning
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        warn("404 not found", ex, req);
        return build(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        warn("400 Validation Failed: " + details, ex, req);
        return build(HttpStatus.BAD_REQUEST, "Validation Failed", details, req);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        String details = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
        warn("400 Constraint Violation: " + details, ex, req);
        return build(HttpStatus.BAD_REQUEST, "Invalid parameters", details, req);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        String mostSpecific = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        warn("400 Unreadable Request Body: " + mostSpecific, ex, req);
        return build(HttpStatus.BAD_REQUEST, "Malformed JSON",
                "The request body could not be parsed. Check JSON formatting and field types.",
                req);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        String msg = String.format("Parameter '%s' value '%s' could not be converted to type %s",
                ex.getName(), ex.getValue(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        warn("400 Type Mismatch: " + msg, ex, req);
        return build(HttpStatus.BAD_REQUEST, "Bad Request", msg, req);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest req) {
        String msg = String.format("Missing required parameter '%s' of type %s", ex.getParameterName(), ex.getParameterType());
        warn("400 Missing Parameter: " + msg, ex, req);
        return build(HttpStatus.BAD_REQUEST, "Bad Request", msg, req);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArg(IllegalArgumentException ex, HttpServletRequest req) {
        warn("400 Bad Request: " + ex.getMessage(), ex, req);
        return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        warn("403 Forbidden: " + ex.getMessage(), ex, req);
        return build(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage(), req);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex, HttpServletRequest req) {
        warn("409 Conflict: " + ex.getMessage(), ex, req);
        return build(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), req);
    }

    // For server-side errors (5XX), we log an error with stack trace. We would probably sanitize the stack trace
    // in a real production system to avoid leaking sensitive info but for our purposes this helps us debug this demo.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        error("500 Internal Server Error", ex, req);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Unexpected error", req);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String title, String detail, HttpServletRequest req) {
        String traceId = MDC.get("traceId"); // see LoggingFilter for how this is set
        ApiError body = new ApiError(title, status.value(), detail, req.getRequestURI(), traceId, Instant.now());
        return ResponseEntity.status(status).body(body);
    }

    // Log a warning with path and trace id
    private void warn(String message, Exception ex, HttpServletRequest req) {
        String traceId = MDC.get("traceId");
        log.warn("{} at {} [{}]: {}", message, req.getRequestURI(), traceId, ex.getMessage());
    }

    // Log an error with stack trace for unexpected 500's
    private void error(String message, Exception ex, HttpServletRequest req) {
        String traceId = MDC.get("traceId");
        log.error("{} at {} [{}]", message, req.getRequestURI(), traceId, ex);
    }
}
