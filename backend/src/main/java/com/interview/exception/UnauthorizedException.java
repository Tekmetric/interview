package com.interview.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authentication is required but not provided or invalid.
 * Results in HTTP 401 Unauthorized response.
 */
public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
    }

    public static UnauthorizedException invalidToken() {
        return new UnauthorizedException("Invalid or expired authentication token");
    }

    public static UnauthorizedException missingToken() {
        return new UnauthorizedException("Authentication token is required");
    }

    public static UnauthorizedException invalidCredentials() {
        return new UnauthorizedException("Invalid username or password");
    }
}