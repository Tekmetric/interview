package com.interview.config;

import com.interview.dto.ErrorDTO;
import com.interview.dto.ValidationErrorDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.Clock;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GlobalExceptionHandler {

  private final Clock clock;

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationErrorDTO> handleValidationExceptions(
      final MethodArgumentNotValidException ex) {
    log.warn("Validation failed", ex);

    final Map<String, String> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .collect(
                Collectors.toMap(
                    FieldError::getField,
                    fieldError ->
                        Optional.ofNullable(fieldError.getDefaultMessage())
                            .orElse("Invalid value")));

    final ValidationErrorDTO dto =
        ValidationErrorDTO.builder()
            .message("Validation failed")
            .timestamp(clock.instant())
            .errors(errors)
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ValidationErrorDTO> handleConstraintViolationException(
      final ConstraintViolationException ex) {
    log.warn("Constraint violation", ex);

    final Map<String, String> errors =
        ex.getConstraintViolations().stream()
            .collect(
                Collectors.toMap(
                    this::resolveViolationKey,
                    this::resolveViolationMessage,
                    (msg1, msg2) -> msg1));

    final ValidationErrorDTO dto =
        ValidationErrorDTO.builder()
            .message("Validation failed")
            .timestamp(clock.instant())
            .errors(errors)
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ValidationErrorDTO> handleHandlerMethodValidationException(
      final HandlerMethodValidationException ex) {
    log.warn("Validation failed", ex);

    final Map<String, String> errors =
        ex.getParameterValidationResults().stream()
            .flatMap(result -> result.getResolvableErrors().stream())
            .collect(
                Collectors.toMap(
                    this::resolveErrorKey, this::resolveErrorMessage, (msg1, msg2) -> msg1));

    final ValidationErrorDTO dto =
        ValidationErrorDTO.builder()
            .message("Validation failed")
            .timestamp(clock.instant())
            .errors(errors)
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorDTO> handleEntityNotFoundException(final EntityNotFoundException ex) {
    log.warn("Resource not found", ex);
    final ErrorDTO dto =
        ErrorDTO.builder().message(ex.getMessage()).timestamp(clock.instant()).build();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dto);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorDTO> handleDataIntegrityViolationException(
      final DataIntegrityViolationException ex) {
    final String message =
        "A data integrity violation occurred. Likely a duplicate or constraint violation.";
    log.warn(message, ex);

    final ErrorDTO dto = ErrorDTO.builder().message(message).timestamp(clock.instant()).build();
    return ResponseEntity.status(HttpStatus.CONFLICT).body(dto);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorDTO> handleGlobalException(final Exception ex) {
    log.error("An unexpected error occurred", ex);

    final ErrorDTO dto =
        ErrorDTO.builder()
            .message("An unexpected error occurred. Please try again later.")
            .timestamp(clock.instant())
            .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(dto);
  }

  private String resolveErrorKey(final MessageSourceResolvable error) {
    return Optional.ofNullable(error.getCodes()).stream()
        .flatMap(Arrays::stream)
        .findFirst()
        .orElse("unknown");
  }

  private String resolveErrorMessage(final MessageSourceResolvable error) {
    return Optional.ofNullable(error.getDefaultMessage()).orElse("Invalid value");
  }

  private String resolveViolationKey(final ConstraintViolation<?> violation) {
    return Optional.ofNullable(violation.getPropertyPath())
        .map(Object::toString)
        .filter(error -> !error.isBlank())
        .orElse("unknown");
  }

  private String resolveViolationMessage(final ConstraintViolation<?> violation) {
    return Optional.ofNullable(violation.getMessage()).orElse("Invalid value");
  }
}
