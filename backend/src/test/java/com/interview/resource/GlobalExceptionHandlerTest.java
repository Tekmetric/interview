package com.interview.resource;

import com.interview.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationExceptionSingleErrorShouldReturnBadRequestWithErrorMessage() {
        // Arrange
        List<String> errorMessages = Collections.singletonList("Invalid input");
        ValidationException exception = new ValidationException(errorMessages);

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleValidationException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("message"));
        assertEquals(errorMessages, response.getBody().get("message"));
    }

    @Test
    void handleValidationExceptionMultipleErrorsShouldReturnBadRequestWithAllErrorMessages() {
        // Arrange
        List<String> errorMessages = Arrays.asList("Invalid input", "Missing required field", "Value out of range");
        ValidationException exception = new ValidationException(errorMessages);

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleValidationException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("message"));
        assertEquals(errorMessages, response.getBody().get("message"));
        assertEquals(3, ((List<?>) response.getBody().get("message")).size());
    }

    @Test
    void handleValidationExceptionNoErrorsShouldReturnBadRequestWithEmptyErrorList() {
        // Arrange
        List<String> errorMessages = Collections.emptyList();
        ValidationException exception = new ValidationException(errorMessages);

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleValidationException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("message"));
        assertEquals(errorMessages, response.getBody().get("message"));
        assertTrue(((List<?>) response.getBody().get("message")).isEmpty());
    }
}
