package com.interview.lucascombs.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(final EntityNotFoundException exception) {
        System.out.println(exception.getMessage());
        return new ResponseEntity<>("Requested resource not found.", HttpStatus.NOT_FOUND);
    }
}
