package com.tekmetric.handler;

import com.tekmetric.CarNotFoundException;
import com.tekmetric.ValidationException;
import com.tekmetric.exceptions.UserNotFoundException;
import com.tekmetric.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CarNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleCarNotFound(
      CarNotFoundException ex, HttpServletRequest request) {
    log.error("Entity is not found: {}", ex.getMessage());
    ErrorResponse body = buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(
      UserNotFoundException ex, HttpServletRequest request) {
    log.error("Entity is not found: {}", ex.getMessage());
    ErrorResponse body = buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ErrorResponse> handleValidationError(
      ValidationException ex, HttpServletRequest request) {

    log.error("Validation failure: {}", ex.getMessage());
    ErrorResponse body = buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<?> handleInvalidFormat(HttpMessageNotReadableException ex) {
    log.error("Validation failure: {}", ex.getMessage());
    ErrorResponse body =
        ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message("Invalid request payload." + ex.getMessage())
            .timestamp(Instant.now())
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  public static ErrorResponse buildErrorResponse(
      HttpStatus status, String ex, HttpServletRequest request) {
    return ErrorResponse.builder()
        .status(status.value())
        .message(ex)
        .path(request.getRequestURI())
        .timestamp(Instant.now())
        .build();
  }
}
