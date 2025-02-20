package com.interview.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Instant;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class DirectorDTOTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testRequiredProperties() {
        DirectorDTO director = new DirectorDTO();

        Set<ConstraintViolation<DirectorDTO>> violations = validator.validate(director);
        assertFalse(violations.isEmpty());

        Set<String> validationMessages = Set.of("First name is mandatory", "Last name is mandatory");

        violations.stream().forEach(violation -> {
            String message = violation.getMessage();
            assertEquals(true, validationMessages.contains(message));
        });

    }

    @Test
    void testAllGettersAndSetters() {
        DirectorDTO director = new DirectorDTO();
        director.setFirstName("John");
        director.setLastName("Doe");
        director.setId(1L);

        Instant now = Instant.now();

        director.setCreatedAt(now);
        director.setUpdatedAt(now);

        assertEquals("John", director.getFirstName());
        assertEquals("Doe", director.getLastName());
        assertEquals(1L, director.getId());
        assertEquals(now, director.getCreatedAt());
        assertEquals(now, director.getUpdatedAt());
    }
}
