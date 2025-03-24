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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import com.interview.runningevents.application.exception.RunningEventNotFoundException;
import com.interview.runningevents.application.exception.ValidationException;
import com.interview.runningevents.infrastructure.web.dto.ErrorResponseDTO;

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
        assertThat(response.getBody().getMessage()).isEqualTo("Running event not found with ID: 123");
        assertThat(response.getBody().getPath()).isEqualTo("/api/events/123");
        assertThat(response.getBody().getTimestamp()).isGreaterThan(0);
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
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid running event data");
        assertThat(response.getBody().getPath()).isEqualTo("/api/events/123");
    }

    @Test
    public void shouldHandleMethodArgumentNotValidException() throws Exception {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError("runningEvent", "name", "Name is required"));
        fieldErrors.add(new FieldError("runningEvent", "dateTime", "Date and time is required"));

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        // When
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleMethodArgumentNotValid(ex, mockRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).isEqualTo("Validation failed. Check 'errors' field for details.");
        assertThat(response.getBody().getPath()).isEqualTo("/api/events/123");
        assertThat(response.getBody().getErrors()).hasSize(2);
        assertThat(response.getBody().getErrors().get(0).getField()).isEqualTo("name");
        assertThat(response.getBody().getErrors().get(0).getMessage()).isEqualTo("Name is required");
        assertThat(response.getBody().getErrors().get(1).getField()).isEqualTo("dateTime");
        assertThat(response.getBody().getErrors().get(1).getMessage()).isEqualTo("Date and time is required");
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
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred. Please try again later.");
        assertThat(response.getBody().getPath()).isEqualTo("/api/events/123");
        assertThat(response.getBody().getErrors()).isEmpty();
    }
}
