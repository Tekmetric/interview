package com.interview.exception;

public abstract sealed class DealershipException extends RuntimeException
    permits CustomerNotFoundException,
            CreditApplicationNotFoundException,
            InvalidApplicationStateException,
            DuplicateResourceException,
            DocumentNotUploadedException {

    protected DealershipException(final String message) {
        super(message);
    }

    protected DealershipException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
