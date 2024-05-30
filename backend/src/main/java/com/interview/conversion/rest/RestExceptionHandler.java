package com.interview.conversion.rest;

import com.interview.exceptions.InvalidInputException;
import com.interview.exceptions.NotFoundException;
import com.interview.exceptions.ServiceException;
import com.interview.exceptions.UnauthorizedException;
import com.interview.external.Error;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  public static final String INVALID_INPUT = "INVALID_INPUT";

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    return createEntity(HttpStatus.BAD_REQUEST, "Malformatted request");
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    return handle(ex);
  }

  private ResponseEntity<Object> handle(Exception ex) {
    if (ex instanceof ServiceException) {
      return handleServiceException((ServiceException) ex);
    }
    return internalServerError();
  }


  @ExceptionHandler(ServiceException.class)
  protected ResponseEntity<Object> handleServiceException(ServiceException ex) {
    if (ex instanceof InvalidInputException) {
      return createEntity(HttpStatus.BAD_REQUEST, getMessage((InvalidInputException) ex));
    }
    if (ex instanceof NotFoundException) {
      return ResponseEntity.notFound().build();
    }
    if (ex instanceof UnauthorizedException) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    return internalServerError();
  }

  private ResponseEntity<Object> internalServerError() {
    return createEntity(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred.  Please retry again");
  }

  private ResponseEntity<Object> createEntity(HttpStatus status, String message) {
    final Error error = new Error();
    error.setErrorCode(status.name());
    error.setHttpStatus(status.value());
    error.setErrorMessage(message);
    return ResponseEntity.status(status.value()).body(error);
  }

  private String getMessage(InvalidInputException invalidInputException) {
    if (invalidInputException.getFields() == null) {
      return null;
    }
    return Arrays.stream(invalidInputException.getFields()).collect(Collectors.joining(","));
  }

}
