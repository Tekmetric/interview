package com.interview.exception;

import java.util.UUID;

public final class CustomerNotFoundException extends DealershipException {

    public CustomerNotFoundException(final UUID id) {
        super("Customer not found with id: " + id);
    }
}
