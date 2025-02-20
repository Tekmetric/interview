package com.interview.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class KeywordDTOTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testRequiredProperty() {
        KeywordDTO keyword = new KeywordDTO();

        Set<ConstraintViolation<KeywordDTO>> violations = validator.validate(keyword);
        assertFalse(violations.isEmpty());

        Set<String> validationMessages = Set.of("Name is required");

        violations.stream().forEach(violation -> {
            String message = violation.getMessage();
            assertEquals(true, validationMessages.contains(message));
        });
    }

    @Test
    public void testAllGettersAndSetters() {
        KeywordDTO keyword = new KeywordDTO();
        keyword.setName("Action");
        keyword.setId(1L);

        assertEquals("Action", keyword.getName());
        assertEquals(1L, keyword.getId());
    }
}
