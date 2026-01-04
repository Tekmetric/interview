package com.interview.exception;

public class RepairJobNotFoundException extends RuntimeException {

    public RepairJobNotFoundException(Long id) {
        super("Repair job not found with id " + id);
    }

    public RepairJobNotFoundException(String message) {
        super(message);
    }
}
