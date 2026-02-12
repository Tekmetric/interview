package com.interview.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when attempting to create a customer that already exists.
 */
public class CustomerAlreadyExistsException extends BusinessException {

    private CustomerAlreadyExistsException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    public CustomerAlreadyExistsException(String email) {
        super("Customer with email " + email + " already exists", HttpStatus.CONFLICT, "CUSTOMER_ALREADY_EXISTS");
    }

    public static CustomerAlreadyExistsException withMessage(String message) {
        return new CustomerAlreadyExistsException(message, HttpStatus.CONFLICT, "CUSTOMER_ALREADY_EXISTS");
    }
}