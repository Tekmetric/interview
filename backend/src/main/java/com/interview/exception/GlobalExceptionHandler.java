package com.interview.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    //todo replace with  () -> new ResponseStatusException(
    //                HttpStatus.NOT_FOUND, "Job posting not found with id: " + id
    //            ));
    private static final String BASE_URI = "https://api.tekmetric.com/problems/";

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());

        problem.setType(URI.create(BASE_URI + "not-found"));
        problem.setTitle("Resource Not Found");
        return problem;
    }

    @ExceptionHandler(IllegalStateTransitionException.class)
    public ProblemDetail handleStateTransition(IllegalStateTransitionException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());

        problem.setType(URI.create(BASE_URI + "illegal-state-transition"));
        problem.setTitle("Illegal State Transition");
        return problem;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        ProblemDetail problem = ex.getBody(); // Spring already started the problem detail for us
        problem.setType(URI.create(BASE_URI + "validation-error"));
        problem.setTitle("Validation Error");

        List<String> violations = ex.getBindingResult().getFieldErrors().stream().map(fe -> fe.getField() + ": " + fe.getDefaultMessage()).sorted().toList();

        // setProperty adds top-level JSON fields (extensions) per RFC 7807
        problem.setProperty("violations", violations);

        return ResponseEntity.status(status).body(problem);
    }

    // ── 3. Constraint Violations (@Validated on Params) ──────

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request contains constraint violations");

        problem.setType(URI.create(BASE_URI + "constraint-violation"));
        problem.setTitle("Constraint Violation");

        List<String> violations = ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage).sorted().toList();

        problem.setProperty("violations", violations);
        return problem;
    }
}