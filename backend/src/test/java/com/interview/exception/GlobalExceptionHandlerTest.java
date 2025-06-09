package com.interview.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleResourceNotFoundException_ShouldReturnNotFoundStatus() {
        // Arrange
        var ex = new ResourceNotFoundException("Resource not found");

        // Act & Assert
        var response = exceptionHandler.handleResourceNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Resource not found", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleValidationException_ShouldReturnBadRequestStatus() {
        // Arrange
        var ex = new ValidationException("Validation error");

        // Act & Assert
        var response = exceptionHandler.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Validation error", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleMappingException_ShouldReturnInternalServerErrorStatus() {
        // Arrange
        var ex = new MappingException("Mapping error");

        // Act & Assert
        var response = exceptionHandler.handleMappingException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("Mapping error"));
        assertNull(response.getBody().getData());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleMethodArgumentNotValid_ShouldReturnBadRequestWithFieldErrors() {
        // Arrange
        var ex = mock(MethodArgumentNotValidException.class);
        var bindingResult = mock(BindingResult.class);
        
        var fieldError1 = new FieldError("object", "field1", "error1");
        var fieldError2 = new FieldError("object", "field2", "error2");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(asList(fieldError1, fieldError2));

        // Act & Assert
        var response = exceptionHandler.handleMethodArgumentNotValid(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        assertEquals(2, response.getBody().getData().size());
        assertEquals("error1", response.getBody().getData().get("field1"));
        assertEquals("error2", response.getBody().getData().get("field2"));
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleConstraintViolationException_ShouldReturnBadRequestStatus() {
        // Arrange
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        var ex = new ConstraintViolationException("Constraint violation", violations);

        // Act & Assert
        var response = exceptionHandler.handleConstraintViolationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("Validation failed"));
        assertNull(response.getBody().getData());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleGlobalException_ShouldReturnInternalServerErrorStatus() {
        // Arrange
        var ex = new Exception("Unexpected error");

        // Act & Assert
        var response = exceptionHandler.handleGlobalException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("An unexpected error occurred. Please try again later.", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        assertNotNull(response.getBody().getTimestamp());
    }
}
