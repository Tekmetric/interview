package com.interview.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a service package is not found.
 */
public class ServicePackageNotFoundException extends BusinessException {

    public ServicePackageNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "SERVICE_PACKAGE_NOT_FOUND");
    }

    public ServicePackageNotFoundException(Long servicePackageId) {
        super("Service package not found with ID: " + servicePackageId, HttpStatus.NOT_FOUND, "SERVICE_PACKAGE_NOT_FOUND");
    }
}