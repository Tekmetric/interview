package com.interview.exception;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class InterviewExceptionHandler
        extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(InterviewExceptionHandler.class);

    @ExceptionHandler({
            RuntimeException.class
    })
    ResponseEntity<Object> handleRuntime(RuntimeException e, WebRequest request) {
        logger.error("Unexpected error on {}", request.getContextPath(), e);
        return super.handleExceptionInternal(e, new ErrorResponse("Unexpected error"),
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler({
            EntityNotFoundException.class,
            NotFoundException.class
    })
    ResponseEntity<Object> handleNotFound(Exception e, WebRequest request) {
        logger.error("Unexpected error {}", request.getContextPath() , e);
        return super.handleExceptionInternal(e, new ErrorResponse("Not found"),
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}
