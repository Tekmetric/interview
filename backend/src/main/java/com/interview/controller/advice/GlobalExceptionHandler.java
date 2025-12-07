package com.interview.controller.advice;

import com.interview.model.exception.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Entity Not Found");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("timestamp", System.currentTimeMillis());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        var fieldError = ex.getFieldErrors().stream()
                .map(fieldErr -> Map.of("name", fieldErr.getField(), "reason", StringUtils.isBlank(fieldErr.getDefaultMessage()) ? "Invalid value" : fieldErr.getDefaultMessage()))
                .toList();

        problemDetail.setProperty("invalid-fields", fieldError);

        return problemDetail;
    }
}
