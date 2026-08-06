package com.interview.exception;

import com.interview.exception.BookStoreExceptions.BookNotFoundException;
import com.interview.exception.BookStoreExceptions.DuplicateIsbnException;
import com.interview.exception.BookStoreExceptions.InvalidSortParameterException;
import com.interview.exception.BookStoreExceptions.OptimisticLockConflictException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String PROBLEM_BASE = "https://bookstore-interview.com/problems/";

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(BookNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, "book-not-found", ex.getMessage());
    }

    @ExceptionHandler(DuplicateIsbnException.class)
    public ResponseEntity<ProblemDetail> handleDuplicateIsbn(DuplicateIsbnException ex) {
        return problem(HttpStatus.CONFLICT, "duplicate-isbn", ex.getMessage());
    }

    @ExceptionHandler(OptimisticLockConflictException.class)
    public ResponseEntity<ProblemDetail> handleOptimisticLock(OptimisticLockConflictException ex) {
        return problem(HttpStatus.CONFLICT, "optimistic-lock-conflict", ex.getMessage());
    }

    @ExceptionHandler(InvalidSortParameterException.class)
    public ResponseEntity<ProblemDetail> handleInvalidSort(InvalidSortParameterException ex) {
        return problem(HttpStatus.BAD_REQUEST, "invalid-sort-parameter", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex) {
        log.error("Unhandled exception", ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "internal-error",
                "An unexpected error occurred. Please try again later.");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalid",
                        (first, second) -> first
                ));

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");
        pd.setType(URI.create(PROBLEM_BASE + "validation-error"));
        pd.setTitle("Validation Error");
        pd.setProperty("errors", fieldErrors);
        pd.setProperty("timestamp", Instant.now());

        log.debug("Validation failure: {}", fieldErrors);
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(pd);
    }

    private ResponseEntity<ProblemDetail> problem(HttpStatus status, String type, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(type);
        pd.setType(URI.create(PROBLEM_BASE + type));
        pd.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(pd);
    }

}