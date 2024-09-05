package com.interview.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class BaseEntity {

    @Id
    @GeneratedValue
    private long id;

    private long createdAtMs;

    private long updatedAtMs;

    public long getId() {
        return id;
    }

    public long getCreatedAtMs() {
        return createdAtMs;
    }

    public long getUpdatedAtMs() {
        return updatedAtMs;
    }

    @PrePersist
    protected void beforeCreate() {
        long ms = System.currentTimeMillis();
        this.createdAtMs = ms;
        this.updatedAtMs = ms;
    }

    @PreUpdate
    protected void beforeUpdate() {
        this.updatedAtMs = System.currentTimeMillis();
    }
}
