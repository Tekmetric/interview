package com.interview.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;



@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiError> handleException(ApplicationException exception) {
        logger.error(exception.getMessage(), exception);
        var apiError  = new ApiError(exception.getMessage());
        return new ResponseEntity<>(apiError,exception.getStatus());
    }
}
