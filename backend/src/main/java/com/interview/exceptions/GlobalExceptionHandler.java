package com.interview.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, "Validation failed")
                .title("Validation Failed")
                .detail("One or more fields in the request body failed validation.")
                .property("errors", errors)
                .build();
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ErrorResponse handleMethodValidation(HandlerMethodValidationException ex) {
        List<ParameterValidationResult> validationResults = ex.getParameterValidationResults();
        Map<String, String> errors = new HashMap<>();
        validationResults.forEach(paramResult -> {
            paramResult.getResolvableErrors().forEach(error -> {
                if (error instanceof FieldError) {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                }
            });
        });

        return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, "Validation failed")
                .title("Validation Failed")
                .detail("One or more request parameters failed validation.")
                .property("errors", errors)
                .build();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ErrorResponse handleNotFound(ResourceNotFoundException ex) {
        return ErrorResponse.create(ex, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ErrorResponse handleIllegalArgument(ResourceAlreadyExistsException ex) {
        return ErrorResponse.create(ex, HttpStatus.CONFLICT, ex.getMessage());
    }
}
