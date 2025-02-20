package com.interview.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.interview.dto.ActorDTO;
import com.interview.models.Actor;

public class ActorTest {
    @Test
    void testActorConstructorFromDTO() {
        ActorDTO actorDTO = new ActorDTO();
        actorDTO.setFirstName("Leonardo");
        actorDTO.setLastName("DiCaprio");

        Actor actor = new Actor(actorDTO);

        assertNotNull(actor);
        assertEquals("Leonardo", actor.getFirstName());
        assertEquals("DiCaprio", actor.getLastName());
        assertNotNull(actor.getCreatedAt());
        assertNotNull(actor.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        Actor actor = new Actor();
        Instant now = Instant.now();

        actor.setFirstName("Robert");
        actor.setLastName("Downey Jr.");
        actor.setUpdatedAt(now);

        assertEquals("Robert", actor.getFirstName());
        assertEquals("Downey Jr.", actor.getLastName());
        assertEquals(now, actor.getUpdatedAt());
    }

    @Test
    void testTimestampsDiffer() {
        Actor actor = new Actor();
        Instant createdAtBeforeSave = actor.getCreatedAt();
        Instant updatedAtBeforeSave = actor.getUpdatedAt();

        actor.setUpdatedAt(Instant.now());

        assertNotNull(actor.getUpdatedAt(), "updatedAt should be set");
        assertNotEquals(createdAtBeforeSave, actor.getUpdatedAt(), "createdAt and updatedAt should differ");
        assertNotEquals(updatedAtBeforeSave, actor.getUpdatedAt(), "updatedAt should be updated");
    }

    @Test
    void testDefaultConstructor() {
        Actor actor = new Actor();

        assertNotNull(actor, "Actor instance should not be null");
        assertNull(actor.getId(), "ID should be null in the default constructor");
        assertNull(actor.getFirstName(), "First name should be null by default");
        assertNull(actor.getLastName(), "Last name should be null by default");
        assertNull(actor.getCreatedAt(), "createdAt should be null by default");
        assertNull(actor.getUpdatedAt(), "updatedAt should be null by default");
    }
}
