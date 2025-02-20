package com.interview.director.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.interview.director.dto.DirectorDTO;

public class DirectorTest {
    @Test
    void testDirectorConstructorFromDTO() {

        DirectorDTO directorDTO = new DirectorDTO();
        directorDTO.setFirstName("Steven");
        directorDTO.setLastName("Spielberg");
        directorDTO.setId(1L);

        Director director = new Director(directorDTO);

        assertNotNull(director);
        assertEquals(1L, director.getId());
        assertEquals("Steven", director.getFirstName());
        assertEquals("Spielberg", director.getLastName());
        assertNotNull(director.getCreatedAt());
        assertNotNull(director.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        Director director = new Director();
        Instant now = Instant.now();

        director.setFirstName("Quentin");
        director.setLastName("Tarantino");
        director.setUpdatedAt(now);
        director.setCreatedAt(now);

        assertEquals("Quentin", director.getFirstName());
        assertEquals("Tarantino", director.getLastName());
        assertEquals(now, director.getUpdatedAt());
        assertEquals(now, director.getCreatedAt());
    }

    @Test
    void testTimestampsDiffer() {
        Director director = new Director();
        Instant createdAtBeforeSave = director.getCreatedAt();
        Instant updatedAtBeforeSave = director.getUpdatedAt();

        director.setUpdatedAt(Instant.now());

        assertNotNull(director.getUpdatedAt(), "updatedAt should be set");
        assertNotEquals(createdAtBeforeSave, director.getUpdatedAt(), "createdAt and updatedAt should differ");
        assertEquals(updatedAtBeforeSave, director.getCreatedAt(), "createdAt should be updated");
    }

    @Test
    void testDefaultConstructor() {
        Director director = new Director();

        assertNotNull(director, "Director instance should not be null");
        assertNull(director.getId(), "ID should be null in the default constructor");
        assertNull(director.getFirstName(), "First name should be null by default");
        assertNull(director.getLastName(), "Last name should be null by default");
        assertNull(director.getCreatedAt(), "createdAt should be null by default");
        assertNull(director.getUpdatedAt(), "updatedAt should be null by default");
    }

    @Test
    void testConstructorAndFieldsNonNullable() {
        Director director = new Director();
        director.setFirstName("Martin");
        director.setLastName("Scorsese");

        assertNotNull(director.getFirstName(), "First name should not be null");
        assertNotNull(director.getLastName(), "Last name should not be null");
    }
}
