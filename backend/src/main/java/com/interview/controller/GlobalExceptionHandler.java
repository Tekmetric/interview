package com.interview.controller;

import com.interview.exception.CarNotFoundException;
import com.interview.exception.CarUpdateIdMismatchException;
import com.interview.exception.DuplicateCarException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CarNotFoundException.class)
    public ResponseEntity<String> handleCarNotFoundException(CarNotFoundException ex) {
        log.warn(ex.getMessage(), ex);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CarUpdateIdMismatchException.class)
    public ResponseEntity<String> handleCarUpdateIdMismatchException(CarUpdateIdMismatchException ex) {
        log.warn(ex.getMessage(), ex);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateCarException.class)
    public ResponseEntity<String> handleDuplicateCarException(DuplicateCarException ex) {
        log.warn(ex.getMessage(), ex);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnexpectedException(Exception ex) {
        log.error("unhandled exception", ex);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
