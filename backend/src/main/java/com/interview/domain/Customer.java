package com.interview.domain;

import java.util.Objects;
import java.util.UUID;

public final class Customer {

    private final UUID id;
    private final String name;
    private final String email;

    public Customer(UUID id, String name, String email) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.email = Objects.requireNonNull(email, "email");
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
