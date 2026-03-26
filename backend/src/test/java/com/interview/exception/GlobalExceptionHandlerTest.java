package com.interview.exception;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturn404ForResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException(UUID.randomUUID());
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleResourceNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().status()).isEqualTo(404);
    }

    @Test
    void shouldReturn409ForResourceAlreadyExists() {
        ResourceAlreadyExistsException ex = new ResourceAlreadyExistsException("Vehicle", "vin", "4T1B11HK0KU800001");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleResourceAlreadyExists(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().status()).isEqualTo(409);
    }

    @Test
    void shouldReturn500ForGenericException() {
        RuntimeException ex = new RuntimeException("Unexpected error");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleGeneric(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().status()).isEqualTo(500);
    }
}