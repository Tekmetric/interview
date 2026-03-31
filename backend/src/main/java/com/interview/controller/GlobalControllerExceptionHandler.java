package com.interview.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        List<ValidationErrorDetail> errorDetails = e.getBindingResult().getFieldErrors().stream().map(
            err -> new ValidationErrorDetail(err.getField(), err.getDefaultMessage())
        ).toList();
        ValidationErrorResponse response = new ValidationErrorResponse("Invalid Request", errorDetails);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    public static class ValidationErrorResponse {
        String message;
        List<ValidationErrorDetail> details;

        ValidationErrorResponse(String message, List<ValidationErrorDetail> details) {
            this.message = message;
            this.details = details;
        }

        public String getMessage() {
            return message;
        }

        public List<ValidationErrorDetail> getDetails() {
            return details;
        }
    }

    public static class ValidationErrorDetail {
        String field;
        String message;

        ValidationErrorDetail(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }
    }
}
