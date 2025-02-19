package com.interview.dtos;

import com.interview.models.Director;
import java.time.Instant;

public class DirectorDTO {
    private long id;
    private String firstName;
    private String lastName;
    private Instant createdAt;
    private Instant updatedAt;

    public DirectorDTO() {
    }

    public DirectorDTO(Director director) {
        this.id = director.getId();
        this.firstName = director.getFirstName();
        this.lastName = director.getLastName();
        this.createdAt = director.getCreatedAt();
        this.updatedAt = director.getUpdatedAt();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
