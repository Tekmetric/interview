package com.interview.service.exception;

import java.util.UUID;

public final class VehicleNotFound extends ServiceException {
    public VehicleNotFound(UUID id) {
        super(String.format("Vehicle with id %s not found", id));
    }
}
