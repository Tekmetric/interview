package com.interview.exception;

import com.interview.persistence.enums.ApplicationStatus;

public final class InvalidApplicationStateException extends DealershipException {

    public InvalidApplicationStateException(final ApplicationStatus current, final ApplicationStatus requested) {
        super("Cannot transition credit application from " + current + " to " + requested);
    }

    public InvalidApplicationStateException(final String message) {
        super(message);
    }
}
