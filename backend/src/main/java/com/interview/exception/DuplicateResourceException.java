package com.interview.exception;

public final class DuplicateResourceException extends DealershipException {

    public DuplicateResourceException(final String message) {
        super(message);
    }

    public DuplicateResourceException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
