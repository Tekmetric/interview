package com.interview.service.exception;

public abstract sealed class ServiceException extends RuntimeException
        permits CustomerNotFound, VehicleNotFound, WorkOrderNotFound {
    protected ServiceException(String message) {
        super(message);
    }
}
