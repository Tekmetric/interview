package com.interview.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class ShopUniqueConstraintViolationExceptionHandler {

    @ExceptionHandler(ShopUniqueConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ShopUniqueConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", ex.getMessage()));
    }
}
