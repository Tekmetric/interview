package com.interview.common.exception;

import com.interview.model.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AutoPartServiceException.class)
    public ResponseEntity<Object> handleException(AutoPartServiceException exception) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setMessage(exception.getMessage());
        errorMessage.status(exception.getHttpStatus().value());
        errorMessage.setTimestamp(OffsetDateTime.now());

        return new ResponseEntity<>(errorMessage, headers, exception.getHttpStatus());
    }

    /*
     * The MethodArgumentNotValidException is thrown when validation on fails the OpenAPI request body parameters.
     * There could/should probably be more detailed handling here to extract specific validation errors since this
     * does reveal some internal service details to the client, but for simplicity I just return the exception message as is.
     * */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setMessage(exception.getMessage());
        errorMessage.status(exception.getStatusCode().value());
        errorMessage.setTimestamp(OffsetDateTime.now());

        return new ResponseEntity<>(errorMessage, headers, exception.getStatusCode());
    }

    // More exception handlers can go here as needed. I kept the list short for simplicity
}
