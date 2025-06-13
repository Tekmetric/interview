package com.interview.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Global exception handler for managing exceptions across the application. This class handles
 * specific exceptions like ResourceNotFoundException, ResourceAlreadyExistsException, and
 * MethodArgumentNotValidException, as well as generic exceptions.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles exceptions when a resource is not found, such as when trying to access a non-existing
   * entity.
   *
   * @param ex the exception containing details about the resource not found
   * @return a response entity with a detailed error response
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
      ResourceNotFoundException ex) {
    log.error("Resource not found: {}", ex.getMessage());
    ErrorResponse error = ErrorResponse.of(
        HttpStatus.NOT_FOUND.value(),
        "Resource Not Found",
        ex.getMessage()
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  /**
   * Handles exceptions when a resource already exists, such as when trying to create a duplicate
   * entry.
   *
   * @param ex the exception containing details about the conflict
   * @return a response entity with a detailed error response
   */
  @ExceptionHandler(ResourceAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleResourceAlreadyExistsException(
      ResourceAlreadyExistsException ex) {
    log.error("Resource already exists: {}", ex.getMessage());
    ErrorResponse error = ErrorResponse.of(
        HttpStatus.CONFLICT.value(),
        "Resource Already Exists",
        ex.getMessage()
    );
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  /**
   * Handles validation exceptions thrown by Spring when request data does not match the expected
   * validation.
   *
   * @param ex the exception containing validation errors
   * @return a response entity with a detailed error response
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    log.error("Validation error: {}", ex.getMessage());
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    ErrorResponse error = ErrorResponse.ofValidation(
        HttpStatus.BAD_REQUEST.value(),
        "Validation Failed",
        "Invalid input data",
        errors
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  /**
   * Handles exceptions when a method argument type does not match the expected type.
   *
   * @param ex the exception containing details about the type mismatch
   * @return a response entity with a detailed error response
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleInvalidParameterExceptions(
      MethodArgumentTypeMismatchException ex) {
    log.error("Invalid parameter error: {}", ex.getMessage());
    ErrorResponse error = ErrorResponse.of(
        HttpStatus.BAD_REQUEST.value(),
        "Invalid Parameter",
        ex.getMessage()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  /**
   * Handles exceptions when the request payload is not readable, such as when JSON parsing fails.
   *
   * @param ex the exception containing details about the unreadable payload
   * @return a response entity with a detailed error response
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleInvalidPayloadExceptions(
      HttpMessageNotReadableException ex) {
    log.error("Invalid payload error: {}", ex.getMessage());
    ErrorResponse error = ErrorResponse.of(
        HttpStatus.BAD_REQUEST.value(),
        "Invalid Payload",
        ex.getMessage()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  /**
   * Handles exceptions when the request content type is not supported, such as when the client
   * sends an unsupported media type.
   *
   * @param ex the exception containing details about the unsupported media type
   * @return a response entity with a detailed error response
   */
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleInvalidContentTypeExceptions(
      HttpMediaTypeNotSupportedException ex) {
    log.error("Invalid content type error: {}", ex.getMessage());
    ErrorResponse error = ErrorResponse.of(
        HttpStatus.BAD_REQUEST.value(),
        "Invalid Content Type",
        ex.getMessage()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  /**
   * Handles exceptions when a required request parameter or path variable is missing.
   *
   * @param ex the exception containing details about the missing parameter
   * @return a response entity with a detailed error response
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingServletRequestParamExceptions(
      MissingServletRequestParameterException ex) {
    log.error("Missing request parameter or path variable error: {}", ex.getMessage());
    ErrorResponse error = ErrorResponse.of(
        HttpStatus.BAD_REQUEST.value(),
        "Missing request parameter or path variable",
        ex.getMessage()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  /**
   * Handles generic exceptions that are not specifically caught by other handlers.
   *
   * @param ex the exception
   * @return a generic error response
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    log.error("Unexpected error: {}", ex.getMessage(), ex);
    ErrorResponse error = ErrorResponse.of(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "Internal Server Error",
        "An unexpected error occurred"
    );
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }

}
