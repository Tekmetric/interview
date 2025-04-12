package com.interview.exception;

import com.interview.dto.ApiResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
    }

    @Test
    void handleResourceNotFoundException_ShouldReturnNotFoundStatus() {
        // Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");

        // Act
        ResponseEntity<ApiResponseDTO<Void>> response = exceptionHandler.handleResourceNotFoundException(ex, webRequest);

        // Assert
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
        ValidationException ex = new ValidationException("Validation error");

        // Act
        ResponseEntity<ApiResponseDTO<Void>> response = exceptionHandler.handleValidationException(ex, webRequest);

        // Assert
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
        MappingException ex = new MappingException("Mapping error");

        // Act
        ResponseEntity<ApiResponseDTO<Void>> response = exceptionHandler.handleMappingException(ex, webRequest);

        // Assert
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
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError fieldError1 = new FieldError("object", "field1", "error1");
        FieldError fieldError2 = new FieldError("object", "field2", "error2");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(java.util.Arrays.asList(fieldError1, fieldError2));

        // Act
        ResponseEntity<ApiResponseDTO<Map<String, String>>> response = 
                exceptionHandler.handleMethodArgumentNotValid(ex, webRequest);

        // Assert
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
        ConstraintViolationException ex = new ConstraintViolationException("Constraint violation", violations);

        // Act
        ResponseEntity<ApiResponseDTO<Void>> response = 
                exceptionHandler.handleConstraintViolationException(ex, webRequest);

        // Assert
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
        Exception ex = new Exception("Unexpected error");

        // Act
        ResponseEntity<ApiResponseDTO<Void>> response = exceptionHandler.handleGlobalException(ex, webRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("An unexpected error occurred. Please try again later.", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        assertNotNull(response.getBody().getTimestamp());
    }
}
