package com.interview.error.exception;

public class AutoshopNotFoundException extends RuntimeException {

    private final Long id;

    public AutoshopNotFoundException(Long id) {
        super("Autoshop not found: id=" + id);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
