package com.interview.controller;

import com.interview.error.NotFoundException;
import com.interview.error.RequestValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RewardsErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(RewardsErrorHandler.class);

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFoundErrorHandler(NotFoundException e){
        logger.error("Not found exception raised: {}", e.toString());
        return e.getMessage();
    }

    @ExceptionHandler(RequestValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String badRequestErrorHandler(RequestValidationException e){
        logger.error("Request validation exception raised: {}", e.toString());
        return e.getMessage();
    }

}
