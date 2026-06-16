package com.interview.exception;

import java.util.UUID;

public final class CreditApplicationNotFoundException extends DealershipException {

    public CreditApplicationNotFoundException(final UUID id) {
        super("Credit application not found with id: " + id);
    }
}
