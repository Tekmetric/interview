package com.interview.exception;

import com.interview.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CarNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCarNotFound(CarNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), null, null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidCarDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCarData(InvalidCarDataException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(), null, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DuplicateVinException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateVin(DuplicateVinException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(), "Conflict", ex.getMessage(), null, null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage())
        );

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), "Validation Failed",
                "One or more fields have validation errors", fieldErrors, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

}
