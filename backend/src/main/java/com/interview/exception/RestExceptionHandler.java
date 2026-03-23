package com.interview.exception;

import com.interview.model.FieldValidationError;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleResourceNotFound(EntityNotFoundException ex) {
        log.warn("Resource not found");
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Resource not found");
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ProblemDetail handleConflict(OptimisticLockException ex) {
        log.warn("Optimistic locking conflict");
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Resource was modified by another request");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleBadRequest(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(BindException.class)
    public ProblemDetail handleValidationException(BindException ex) {
        log.warn("Validation failed with {} error(s)", ex.getFieldErrorCount());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problemDetail.setProperty("errors", ex.getFieldErrors().stream()
                .map(fieldError -> new FieldValidationError(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList());
        return problemDetail;
    }
}
