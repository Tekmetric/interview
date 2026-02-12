package com.interview.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a customer is not found.
 */
public class CustomerNotFoundException extends BusinessException {

    public CustomerNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "CUSTOMER_NOT_FOUND");
    }

    public CustomerNotFoundException(Long customerId) {
        super("Customer not found with ID: " + customerId, HttpStatus.NOT_FOUND, "CUSTOMER_NOT_FOUND");
    }
}
