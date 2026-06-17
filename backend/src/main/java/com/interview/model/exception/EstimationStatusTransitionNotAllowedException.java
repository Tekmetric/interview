package com.interview.model.exception;

public class EstimationStatusTransitionNotAllowedException extends RuntimeException {
    public EstimationStatusTransitionNotAllowedException(String message) {
        super(message);
    }
}
