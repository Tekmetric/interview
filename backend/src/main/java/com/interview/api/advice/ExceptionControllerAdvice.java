package com.interview.api.advice;

import com.interview.exception.ServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice /*extends ResponseEntityExceptionHandler */{

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<String> serviceException(ServiceException ex) {
        return ResponseEntity
                .status(HttpUtils.fromExceptionReason(ex.getReason()))
                .body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exception() {
        return ResponseEntity
                .internalServerError()
                .body("Something went wrong. Please try again later");
    }

}
