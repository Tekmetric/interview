package com.interview.runningevents.infrastructure.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.interview.runningevents.application.exception.RunningEventNotFoundException;
import com.interview.runningevents.application.exception.ValidationException;
import com.interview.runningevents.infrastructure.web.dto.ErrorResponseDTO;

/**
 * Global exception handler for REST API controllers.
 * Converts exceptions to standardized error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles RunningEventNotFoundException.
     * Returns HTTP 404 Not Found.
     *
     * @param ex The exception
     * @param request The web request
     * @return Error response with 404 status
     */
    @ExceptionHandler(RunningEventNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleRunningEventNotFoundException(
            RunningEventNotFoundException ex, WebRequest request) {

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles ValidationException.
     * Returns HTTP 400 Bad Request.
     *
     * @param ex The exception
     * @param request The web request
     * @return Error response with 400 status
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(ValidationException ex, WebRequest request) {

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles MethodArgumentNotValidException for @Valid annotation validation failures.
     * Returns HTTP 400 Bad Request with field-specific validation errors.
     *
     * @param ex The exception
     * @param request The web request
     * @return Error response with 400 status and validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {

        // Extract field-specific validation errors
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        // Create the error response with general info
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed. Check 'errors' field for details.")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        // Add each field-specific validation error
        fieldErrors.forEach(
                fieldError -> errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage()));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all other uncaught exceptions.
     * Returns HTTP 500 Internal Server Error.
     *
     * @param ex The exception
     * @param request The web request
     * @return Error response with 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleAllUncaughtException(Exception ex, WebRequest request) {

        // Log the error
        logger.error("Unhandled exception", ex);

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred. Please try again later.")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
