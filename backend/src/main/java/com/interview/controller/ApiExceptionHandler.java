package com.interview.controller;

import com.interview.service.AthleteNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(AthleteNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(AthleteNotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "not_found");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleBodyValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            String field = fieldError.getField();
            if (!fieldErrors.containsKey(field)) {
                fieldErrors.put(field, fieldError.getDefaultMessage());
            }
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "validation_error");
        body.put("message", "Request validation failed");
        body.put("fieldErrors", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "validation_error");
        body.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleBadJson(HttpMessageNotReadableException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "bad_request");
        body.put("message", "Malformed JSON or invalid enum value");
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "bad_request");
        body.put("message", "Invalid value for parameter");
        body.put("parameter", ex.getName());
        body.put("value", ex.getValue());
        body.put("expectedType", ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : null);
        return ResponseEntity.badRequest().body(body);
    }
}
