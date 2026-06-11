package com.interview.service.exception;

import java.util.UUID;

public final class CustomerNotFound extends ServiceException {
    public CustomerNotFound(UUID id) {
        super(String.format("Customer with id %s not found", id));
    }
}
