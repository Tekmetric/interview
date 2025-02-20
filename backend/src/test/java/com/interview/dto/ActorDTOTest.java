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

public class ActorDTOTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testRequiredProperties() {
        ActorDTO actor = new ActorDTO();

        Set<ConstraintViolation<ActorDTO>> violations = validator.validate(actor);
        assertFalse(violations.isEmpty());

        Set<String> validationMessages = Set.of("First name is mandatory", "Last name is mandatory");

        violations.stream().forEach(violation -> {
            String message = violation.getMessage();
            assertEquals(true, validationMessages.contains(message));
        });

    }

    @Test
    public void testAllGettersAndSetters() {
        ActorDTO actor = new ActorDTO();
        actor.setFirstName("John");
        actor.setLastName("Doe");
        actor.setId(1L);

        Instant now = Instant.now();

        actor.setCreatedAt(now);
        actor.setUpdatedAt(now);

        assertEquals("John", actor.getFirstName());
        assertEquals("Doe", actor.getLastName());
        assertEquals(1L, actor.getId());
        assertEquals(now, actor.getCreatedAt());
        assertEquals(now, actor.getUpdatedAt());
    }
}
