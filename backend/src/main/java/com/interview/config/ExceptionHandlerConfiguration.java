package com.interview.config;

import com.interview.controller.exception.ErrorResponse;
import com.interview.controller.exception.TekmetricDomainException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class ExceptionHandlerConfiguration {
    @ExceptionHandler(value = {TekmetricDomainException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> badRequest(Exception ex) {
        return ResponseEntity.status(400).body(new ErrorResponse(400, ex.getMessage()));
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> unKnownException(Exception ex) {
        return ResponseEntity.status(400).body(new ErrorResponse(404, ex.getMessage()));
    }
}