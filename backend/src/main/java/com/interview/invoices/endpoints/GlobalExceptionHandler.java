package com.interview.invoices.endpoints;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.interview.invoices.services.InvoiceServiceException;
import com.interview.invoices.services.InvoiceServiceException.ErrorCode;
import com.interview.invoices.api.v1.ErrorResponse;
import com.interview.invoices.api.v1.ImmutableErrorResponse;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  private static final Map<InvoiceServiceException.ErrorCode, HttpStatus> ERROR_CODE_MAPPER = new HashMap<>();
  static {
    // TODO Better to use Guava's ImmutableMap.of(...) until migrate to newer Java version.
    ERROR_CODE_MAPPER.put(ErrorCode.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    ERROR_CODE_MAPPER.put(ErrorCode.ENTITY_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
    ERROR_CODE_MAPPER.put(ErrorCode.ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND);
    ERROR_CODE_MAPPER.put(ErrorCode.OPERATION_NOT_ALLOWED, HttpStatus.FORBIDDEN);
    ERROR_CODE_MAPPER.put(ErrorCode.CONFLICT, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(InvoiceServiceException.class)
  public ResponseEntity<ErrorResponse> handleInvoiceServiceException(InvoiceServiceException e) {
    ErrorResponse body = ImmutableErrorResponse.of(e.errorCode().name(), e.getMessage());
    return ResponseEntity.status(ERROR_CODE_MAPPER.get(e.errorCode())).body(body);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
    ErrorResponse body = ImmutableErrorResponse.of("INVALID_REQUEST", e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
    String message = String.format("Invalid parameter '%s'.", e.getName());
    ErrorResponse body = ImmutableErrorResponse.of("INVALID_REQUEST", message);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
    String errorMessage;
    if (e.getCause() instanceof ValueInstantiationException) {
      errorMessage = e.getCause().getMessage();
    } else {
      errorMessage = e.getMessage();
    }
    ErrorResponse body = ImmutableErrorResponse.of("INVALID_REQUEST", errorMessage);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
    LOG.error(e.getMessage(), e);
    ErrorResponse body = ImmutableErrorResponse.of("INTERNAL_ERROR", "Unexpected server error.");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }
}
