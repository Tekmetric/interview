package com.tekmetric.handler;

import static org.junit.jupiter.api.Assertions.*;

import com.tekmetric.CarNotFoundException;
import com.tekmetric.ValidationException;
import com.tekmetric.exceptions.UserNotFoundException;
import com.tekmetric.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void handleCarNotFound_returns404AndErrorBody() {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    Mockito.when(request.getRequestURI()).thenReturn("/cars/123");

    ResponseEntity<ErrorResponse> response =
        handler.handleCarNotFound(new CarNotFoundException("not found"), request);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals(404, response.getBody().getStatus());
    assertEquals("/cars/123", response.getBody().getPath());
    assertEquals("not found", response.getBody().getMessage());
  }

  @Test
  void handleUserNotFound_returns404() {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    Mockito.when(request.getRequestURI()).thenReturn("/user");

    ResponseEntity<ErrorResponse> response =
        handler.handleUserNotFound(new UserNotFoundException("not found"), request);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void handleUserValidationError_returns400() {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    Mockito.when(request.getRequestURI()).thenReturn("/user");

    ResponseEntity<ErrorResponse> response =
        handler.handleValidationError(new ValidationException("bad data"), request);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }
}
