package com.interview.controller.advice;

import com.interview.controller.validation.ValidationError;
import com.interview.exceptions.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@RestControllerAdvice
@Slf4j
public class ErrorHandlingControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String productNotFoundHandler(ProductNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(ConstraintViolationException.class)
    public List<ValidationError> handleConstraintViolation(ConstraintViolationException ex) {
        log.debug("Constraint violation exception encountered: {}", ex.getConstraintViolations(), ex);

        return buildValidationErrors(ex.getConstraintViolations());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.debug("Found {} error while validating {}", ex.getBindingResult().getErrorCount(), ex.getParameter().getMethod().getName(), ex);

        return handleExceptionInternal(ex, buildValidationErrors(ex.getBindingResult()), headers, status, request);
    }

    private List<ValidationError> buildValidationErrors(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(objectError ->
                        ValidationError.builder()
                                .field(((FieldError) objectError).getField())
                                .error(objectError.getDefaultMessage())
                                .build()
                ).collect(toList());

    }


    private List<ValidationError> buildValidationErrors(Set<ConstraintViolation<?>> violations) {
        return violations.
                stream().
                map(violation ->
                        ValidationError.builder().
                                field(
                                        Objects.requireNonNull(StreamSupport.stream(
                                                                violation.getPropertyPath().spliterator(), false).
                                                        reduce((first, second) -> second).
                                                        orElse(null)).
                                                toString()
                                ).
                                error(violation.getMessage()).
                                build()).
                collect(toList());
    }

}
