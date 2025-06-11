package com.interview.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents a standardized error response structure for API errors. This class encapsulates
 * details about the error, including the timestamp, HTTP status, error type, message, request path,
 * and any validation errors.
 *
 * @param timestamp        the time when the error occurred
 * @param status           the HTTP status code of the error
 * @param error            the type of error (e.g., "Bad Request", "Not Found")
 * @param message          a human-readable message describing the error
 * @param validationErrors a map of validation errors, if any, where the key is the field name
 */
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    Map<String, String> validationErrors
) {

  // Factory method for basic errors
  public static ErrorResponse of(int status, String error, String message) {
    return new ErrorResponse(LocalDateTime.now(), status, error, message, null);
  }

  // Factory method for validation errors
  public static ErrorResponse ofValidation(int status, String error, String message,
      Map<String, String> validationErrors) {
    return new ErrorResponse(LocalDateTime.now(), status, error, message, validationErrors);
  }
}
