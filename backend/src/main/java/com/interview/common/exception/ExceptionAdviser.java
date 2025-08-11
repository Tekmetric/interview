package com.interview.common.exception;

import com.interview.service.validator.ValidatorProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Log4j2
@RestControllerAdvice
@RequiredArgsConstructor
@Order(HIGHEST_PRECEDENCE)
public class ExceptionAdviser {

    private static final String VALIDATION_FAILED_MESSAGE = "Validation failed";

    /**
     * Handles custom business/bean validation errors thrown by {@link ValidatorProvider}.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex, HttpServletRequest request) {
        List<String> codes = null;
        if (ex.getErrorCode() != null && !ex.getErrorCode().isBlank()) {
            codes = Arrays.stream(ex.getErrorCode().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .codes(codes)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handles {@link Valid} request-body failures (binding/field errors).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                      HttpServletRequest req) {
        List<FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> FieldViolation.builder()
                        .field(fe.getField())
                        .message(fe.getDefaultMessage())
                        .code(fe.getCode())
                        .rejectedValue(fe.getRejectedValue())
                        .build())
                .toList();

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(VALIDATION_FAILED_MESSAGE)
                .path(req.getRequestURI())
                .violations(violations)
                .build();

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Handles constraint violations from method parameters/path/query validation.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                   HttpServletRequest req) {
        List<FieldViolation> violations = ex.getConstraintViolations().stream()
                .map(cv -> FieldViolation.builder()
                        .field(cv.getPropertyPath() != null ? cv.getPropertyPath().toString() : null)
                        .message(cv.getMessage())
                        .code(cv.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName())
                        .rejectedValue(cv.getInvalidValue())
                        .build())
                .toList();

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(VALIDATION_FAILED_MESSAGE)
                .path(req.getRequestURI())
                .violations(violations)
                .build();

        return ResponseEntity.badRequest().body(body);
    }
}
