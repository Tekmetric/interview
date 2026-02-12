package com.interview.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when optimistic locking conflict occurs.
 * Indicates that the entity was modified by another user/process.
 */
public class OptimisticLockingException extends BusinessException {

    public OptimisticLockingException(String entityName, Long entityId) {
        super(
            String.format("The %s (ID: %d) was modified by another user. Please refresh and try again.",
                entityName, entityId),
            HttpStatus.CONFLICT,
            "OPTIMISTIC_LOCK_ERROR"
        );
    }

    public OptimisticLockingException(String message) {
        super(message, HttpStatus.CONFLICT, "OPTIMISTIC_LOCK_ERROR");
    }
}