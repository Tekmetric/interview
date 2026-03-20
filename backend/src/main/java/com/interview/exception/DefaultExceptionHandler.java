package com.interview.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionDetails> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ExceptionDetails> handleResourceAlreadyExists(ResourceAlreadyExistsException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDetails> handleGlobalException(Exception ex, WebRequest request) {
        return buildErrorResponse("Internal Server Error", request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDetails> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        ExceptionDetails errorDetails = new ExceptionDetails(
                LocalDateTime.now(),
                "Validation Failed: " + errorMessage,
                "Check your input fields"
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ExceptionDetails> buildErrorResponse(String message, WebRequest request, HttpStatus status) {
        ExceptionDetails ExceptionDetails = new ExceptionDetails(
                LocalDateTime.now(),
                message,
                request.getDescription(false)
        );
        return new ResponseEntity<>(ExceptionDetails, status);
    }
}