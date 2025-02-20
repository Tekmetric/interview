package com.interview.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.interview.exceptions.NotFoundException;
import com.interview.exceptions.UniqueConstraintViolationException;
import com.interview.handlers.GlobalExceptionHandler;

public class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleUniqueConstraintViolationException() {
        // Arrange
        UniqueConstraintViolationException exception = new UniqueConstraintViolationException("Email already exists");

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleUniqueConstraintViolation(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Conflict", body.get("error"));
        assertEquals("Email already exists", body.get("message"));
    }

    @Test
    void testHandleValidationExceptions() {
        // Arrange
        // Mock MethodArgumentNotValidException
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);

        // Mock BindingResult
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("MovieDTO", "title", "Title is required");

        // Ensure bindingResult returns the mocked FieldError
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleValidationExceptions(exception);

        // Assert
        assertEquals(400, response.getStatusCodeValue()); // BAD_REQUEST status
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("Title is required", body.get("title"));
    }

    @Test
    void testHandleNotFoundException() {
        // Arrange
        NotFoundException exception = new NotFoundException("Movie not found");

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleNotFoundException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Movie not found", body.get("message"));
    }
}
