package com.interview.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleException(IllegalArgumentException ex) {
        log.error(ex.getMessage(), ex);
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(BindException.class)
    public ProblemDetail handleException(BindException ex) {
        final ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("timestamp", Instant.now());

        List<ValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationError(
                        error.getField(),
                        error.getRejectedValue(),
                        error.getDefaultMessage()))
                .sorted(Comparator.comparing(ValidationError::field))
                .toList();
        problemDetail.setProperty("errors", errors);


        return problemDetail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleException(ConstraintViolationException ex) {
        final ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("timestamp", Instant.now());

        List<ValidationError> errors = ex.getConstraintViolations().stream()
                .map(violation -> new ValidationError(
                        violation.getPropertyPath().toString(),
                        violation.getInvalidValue(),
                        violation.getMessage()))
                .sorted(Comparator.comparing(ValidationError::field))
                .toList();
        problemDetail.setProperty("errors", errors);


        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleException(HttpMessageNotReadableException ex) {
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    public record ValidationError(String field, Object value, String details) {}
}
