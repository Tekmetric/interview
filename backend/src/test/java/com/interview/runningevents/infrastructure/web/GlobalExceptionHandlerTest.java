package com.interview.runningevents.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.interview.runningevents.application.exception.RunningEventNotFoundException;
import com.interview.runningevents.application.exception.ValidationException;
import com.interview.runningevents.infrastructure.web.dto.ErrorResponseDTO;

/**
 * Tests for the enhanced GlobalExceptionHandler class.
 */
public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest mockRequest;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        mockRequest = mock(WebRequest.class);
        when(mockRequest.getDescription(false)).thenReturn("uri=/api/events/123");
    }

    @Test
    public void shouldHandleRunningEventNotFoundException() {
        // Given
        RunningEventNotFoundException ex = new RunningEventNotFoundException(123L);

        // When
        ResponseEntity<ErrorResponseDTO> response =
                exceptionHandler.handleRunningEventNotFoundException(ex, mockRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError()).isEqualTo("Not Found");
        assertThat(response.getBody().getMessage()).isEqualTo("Running event not found with ID: 123");
        assertThat(response.getBody().getPath()).isEqualTo("/api/events/123");
        assertThat(response.getBody().getTimestamp()).isGreaterThan(0);
        assertThat(response.getBody().getDetails()).isEmpty();
    }

    @Test
    public void shouldHandleValidationException() {
        // Given
        ValidationException ex = new ValidationException("Invalid running event data");

        // When
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleValidationException(ex, mockRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Bad Request");
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid running event data");
        assertThat(response.getBody().getPath()).isEqualTo("/api/events/123");
        assertThat(response.getBody().getDetails()).isEmpty();
    }

    @Test
    public void shouldHandleMethodArgumentNotValidException() throws Exception {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError("runningEvent", "name", "Name is required"));
        fieldErrors.add(new FieldError("runningEvent", "dateTime", "Date and time is required"));

        List<ObjectError> globalErrors = new ArrayList<>();
        globalErrors.add(new ObjectError("runningEvent", "Event validation failed"));

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        when(bindingResult.getGlobalErrors()).thenReturn(globalErrors);

        // When
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleMethodArgumentNotValid(ex, mockRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Bad Request");
        assertThat(response.getBody().getMessage()).contains("Validation failed");
        assertThat(response.getBody().getPath()).isEqualTo("/api/events/123");
        assertThat(response.getBody().getDetails()).hasSize(3);

        // Check field-specific errors
        assertThat(response.getBody().getDetails().stream()
                        .anyMatch(e ->
                                e.getField().equals("name") && e.getMessage().equals("Name is required")))
                .isTrue();
        assertThat(response.getBody().getDetails().stream()
                        .anyMatch(e -> e.getField().equals("dateTime")
                                && e.getMessage().equals("Date and time is required")))
                .isTrue();
        // Check global errors
        assertThat(response.getBody().getDetails().stream()
                        .anyMatch(e -> e.getField().equals("runningEvent")
                                && e.getMessage().equals("Event validation failed")))
                .isTrue();
    }

    @Test
    public void shouldHandleTypeMismatchException() {
        // Given
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getValue()).thenReturn("abc");
        when(ex.getPropertyName()).thenReturn("id");
        when(ex.getRequiredType()).thenReturn((Class) Long.class);

        // When
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleTypeMismatch(ex, mockRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Bad Request");
        assertThat(response.getBody().getMessage()).isEqualTo("Type mismatch");
        assertThat(response.getBody().getPath()).isEqualTo("/api/events/123");
        assertThat(response.getBody().getDetails()).hasSize(1);
        assertThat(response.getBody().getDetails().get(0).getMessage()).contains("abc is not a valid value for id");
        assertThat(response.getBody().getDetails().get(0).getMessage()).contains("Expected type: Long");
    }

    @Test
    public void shouldHandleHttpMessageNotReadableException() {
        // Given
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        InvalidFormatException cause = mock(InvalidFormatException.class);
        when(ex.getCause()).thenReturn(cause);
        when(cause.getMessage())
                .thenReturn(
                        "Cannot deserialize value of type `java.lang.Long` from String \"invalid\": not a valid Long value");

        // When
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleHttpMessageNotReadable(ex, mockRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Bad Request");
        assertThat(response.getBody().getMessage()).isEqualTo("Malformed JSON request");
        assertThat(response.getBody().getPath()).isEqualTo("/api/events/123");
        assertThat(response.getBody().getDetails()).hasSize(1);
        assertThat(response.getBody().getDetails().get(0).getMessage()).contains("Cannot deserialize value");
    }

    @Test
    public void shouldHandleMissingServletRequestParameterException() {
        // Given
        MissingServletRequestParameterException ex = mock(MissingServletRequestParameterException.class);
        when(ex.getParameterName()).thenReturn("startDate");
        when(ex.getParameterType()).thenReturn("Long");
        when(ex.getMessage()).thenReturn("Required Long parameter 'startDate' is not present");

        // When
        ResponseEntity<ErrorResponseDTO> response =
                exceptionHandler.handleMissingServletRequestParameter(ex, mockRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Bad Request");
        assertThat(response.getBody().getMessage()).isEqualTo("Missing required parameter");
        assertThat(response.getBody().getPath()).isEqualTo("/api/events/123");
        assertThat(response.getBody().getDetails()).hasSize(1);
        assertThat(response.getBody().getDetails().get(0).getField()).isEqualTo("startDate");
        assertThat(response.getBody().getDetails().get(0).getMessage()).isEqualTo("Parameter is required");
    }

    @Test
    public void shouldHandleNoHandlerFoundException() {
        // Given
        NoHandlerFoundException ex = mock(NoHandlerFoundException.class);
        when(ex.getHttpMethod()).thenReturn("GET");
        when(ex.getRequestURL()).thenReturn("/api/invalid-path");

        // When
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleNoHandlerFoundException(ex, mockRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError()).isEqualTo("Not Found");
        assertThat(response.getBody().getMessage()).isEqualTo("No handler found for GET /api/invalid-path");
        assertThat(response.getBody().getPath()).isEqualTo("/api/events/123");
    }

    @Test
    public void shouldHandleGenericException() {
        // Given
        Exception ex = new RuntimeException("Something went wrong");

        // When
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleAllUncaughtException(ex, mockRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred. Please try again later.");
        assertThat(response.getBody().getPath()).isEqualTo("/api/events/123");
    }
}
