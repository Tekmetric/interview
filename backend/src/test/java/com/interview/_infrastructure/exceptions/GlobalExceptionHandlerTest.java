package com.interview._infrastructure.exceptions;

import com.interview._infrastructure.domain.model.CustomError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleGenericException() {
        RuntimeException exception = new RuntimeException("Error Message");

        ResponseEntity<CustomError> result = globalExceptionHandler.handleGenericException(exception, httpServletRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        CustomError error = result.getBody();
        assertNotNull(error);
        assertEquals("Error Message", error.getMessage());
        assertEquals("Internal Server Error", error.getError());
    }

    @Test
    void handleBadRequest() {
        BadRequestException exception = new BadRequestException("Error Message");

        ResponseEntity<CustomError> result = globalExceptionHandler.handleBadRequest(exception, httpServletRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        CustomError error = result.getBody();
        assertNotNull(error);
        assertEquals("Error Message", error.getMessage());
        assertEquals("Bad Request", error.getError());
    }

    @Test
    void handleNotFound() {
        NotFoundException exception = new NotFoundException("Not Found Message");

        ResponseEntity<CustomError> result = globalExceptionHandler.handleNotFound(exception, httpServletRequest);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        CustomError error = result.getBody();
        assertNotNull(error);
        assertEquals("Not Found Message", error.getMessage());
        assertEquals("Not Found", error.getError());
    }

    @Test
    void handleUnauthorized() {
        UnauthorizedException exception = new UnauthorizedException("Unauthorized message");

        ResponseEntity<CustomError> result = globalExceptionHandler.handleUnauthorized(exception, httpServletRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        CustomError error = result.getBody();
        assertNotNull(error);
        assertEquals("Unauthorized message", error.getMessage());
        assertEquals("Unauthorized", error.getError());
    }
}
