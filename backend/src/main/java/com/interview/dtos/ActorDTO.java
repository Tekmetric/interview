package com.interview.dtos;

import java.time.Instant;

import com.interview.models.Actor;

public class ActorDTO {
    private long id;
    private String first_name;
    private String last_name;
    private Instant createdAt;
    private Instant updatedAt;

    public ActorDTO() {
    }

    public ActorDTO(Actor actor) {
        this.id = actor.getId();
        this.first_name = actor.getFirstName();
        this.last_name = actor.getLastName();
        this.createdAt = actor.getCreatedAt();
        this.updatedAt = actor.getUpdatedAt();
    }

    public long getId() {
        return id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setId(long id) {
        this.id = id;
    }
}
