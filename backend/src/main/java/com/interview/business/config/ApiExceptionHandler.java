package com.interview.business.config;


import com.interview.core.api.payloads.ErrorResponse;
import com.interview.core.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> onApiException(ApiException exception) {

        return ResponseEntity.status(exception.getStatus()).body(
                new ErrorResponse(
                        exception.getCode(),
                        exception.getMessage()
                )
        );
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponse onValidationException(AccessDeniedException exception) {

        return new ErrorResponse(
                "Access_Denied",
                "You need to login",
                exception.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse onUnhandledExceptions(Exception exception) {

        return new ErrorResponse(
                "Unhandled",
                "Something went wrong",
                exception.getMessage()
        );
    }
}
