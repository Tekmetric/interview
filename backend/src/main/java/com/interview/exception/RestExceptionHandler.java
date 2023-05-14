package com.interview.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage resourceNotFoundException(ResourceNotFoundException exception) {

        return new ErrorMessage(
                new Date(),
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorMessage duplicateResourceException(DuplicateResourceException exception) {

        return new ErrorMessage(
                new Date(),
                HttpStatus.CONFLICT.value(),
                exception.getMessage());
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handle(final BindException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        StringBuilder errorMessage = new StringBuilder();
        for (FieldError error : fieldErrors) {
            String fieldName = error.getField();
            String message = error.getDefaultMessage();

            errorMessage.append(fieldName).append(": ").append(message).append("; ");
        }

        return new ErrorMessage(
                new Date(),
                HttpStatus.BAD_REQUEST.value(),
                errorMessage.toString());
    }

}
