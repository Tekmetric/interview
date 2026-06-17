package com.interview.controller.advice;

import com.interview.model.exception.FieldNotAllowedInSortException;
import com.interview.model.exception.ResourceNotFoundException;
import com.interview.model.exception.EstimationStatusTransitionNotAllowedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ResourceNotFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleResourceNotFoundException(Exception ex) {
        log.warn("Resource not found: ", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Entity Not Found");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("timestamp", System.currentTimeMillis());
        return problemDetail;
    }

    @ExceptionHandler(EstimationStatusTransitionNotAllowedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleEstimationStatusTransitionException(EstimationStatusTransitionNotAllowedException ex) {
        log.warn("Conflict exception: ", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Cannot request another estimation");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("timestamp", System.currentTimeMillis());
        return problemDetail;
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        log.warn("Forbidden exception: ", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setTitle("Forbidden");
        problemDetail.setDetail("Forbidden");
        problemDetail.setProperty("timestamp", System.currentTimeMillis());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Bad request");
        problemDetail.setDetail("Bad request");
        problemDetail.setProperty("timestamp", System.currentTimeMillis());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("Validation exception: ", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        var fieldError = ex.getFieldErrors().stream()
                .map(fieldErr -> Map.of("name", fieldErr.getField(), "reason", StringUtils.isBlank(fieldErr.getDefaultMessage()) ? "Invalid value" : fieldErr.getDefaultMessage()))
                .toList();

        problemDetail.setProperty("invalid-fields", fieldError);

        return problemDetail;
    }

    @ExceptionHandler(FieldNotAllowedInSortException.class)
    public ProblemDetail handleFieldNotAllowedInSortException(FieldNotAllowedInSortException ex) {
        log.warn("Sort validation exception: ", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Cannot request another estimation");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("timestamp", System.currentTimeMillis());

        return problemDetail;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentialsException(BadCredentialsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Bad credentials");
        problemDetail.setDetail("Bad credentials");
        problemDetail.setProperty("timestamp", System.currentTimeMillis());
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Generic exception: ", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setDetail("Something went wrong");

        return problemDetail;
    }
}
