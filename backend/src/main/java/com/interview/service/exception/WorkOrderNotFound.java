package com.interview.service.exception;

import java.util.UUID;

public final class WorkOrderNotFound extends ServiceException {
    public WorkOrderNotFound(UUID id) {
        super(String.format("Work order with id %s not found", id));
    }
}
