package com.interview.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import com.interview.config.dto.ApiExceptionResponse;
import com.interview.exception.ApplicationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerConfiguration {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiExceptionResponse> handleApplicationException(
        final ApplicationException exception
    ) {
        log.warn("Application exception occurred.", exception);

        return ResponseEntity
            .status(exception.getStatus())
            .body(new ApiExceptionResponse(exception.getCode(),exception.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiExceptionResponse> handleAuthenticationException(
        final AuthenticationException exception
    ) {
        log.warn("Authentication exception occurred.", exception);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiExceptionResponse("NOT_AUTHENTICATED", exception.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiExceptionResponse> handleAccessDeniedException(
        final AccessDeniedException exception
    ) {
        log.warn("Access denied exception occurred.", exception);

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiExceptionResponse("MISSING_PERMISSIONS", exception.getMessage()));
    }

    @ExceptionHandler(BadRequest.class)
    public ResponseEntity<ApiExceptionResponse> handleBadRequestException(
        final BadRequest exception
    ) {
        log.warn("Bad request exception occurred.", exception);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiExceptionResponse("UNEXPECTED_REQUEST", exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiExceptionResponse> handleSystemException(
        final Exception exception
    ) {
        log.error("System exception occurred.", exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiExceptionResponse("UNKNOWN_EXCEPTION", exception.getMessage()));
    }
}
