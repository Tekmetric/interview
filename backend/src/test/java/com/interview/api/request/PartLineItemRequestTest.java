package com.interview.api.request;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PartLineItemRequestTest {

    private final Validator validator;

    PartLineItemRequestTest() {
        try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    void testHappyPath() {
        final Set<ConstraintViolation<PartLineItemRequest>> violations =
                validator.validate(new PartLineItemRequest("Oil Filter", 1, UUID.randomUUID()));
        assertThat(violations).isEmpty();
    }

    @Test
    void testBlankName() {
        final Set<ConstraintViolation<PartLineItemRequest>> violations =
                validator.validate(new PartLineItemRequest("", 1, UUID.randomUUID()));
        assertThat(violations).extracting(ConstraintViolation::getMessage).contains("must not be blank");
    }

    @Test
    void testQuantityZero() {
        final Set<ConstraintViolation<PartLineItemRequest>> violations =
                validator.validate(new PartLineItemRequest("Oil Filter", 0, UUID.randomUUID()));
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("must be greater than or equal to 1");
    }

    @Test
    void testNullPartNumber() {
        final Set<ConstraintViolation<PartLineItemRequest>> violations =
                validator.validate(new PartLineItemRequest("Oil Filter", 1, null));
        assertThat(violations).extracting(ConstraintViolation::getMessage).contains("must not be null");
    }

    @Test
    void testNameExceedsMaxLength() {
        final String longName = "a".repeat(256);
        final Set<ConstraintViolation<PartLineItemRequest>> violations =
                validator.validate(new PartLineItemRequest(longName, 1, UUID.randomUUID()));
        assertThat(violations).extracting(ConstraintViolation::getMessage).contains("size must be between 0 and 255");
    }

    @Test
    void testNameAtMaxLength() {
        final String maxName = "a".repeat(255);
        final Set<ConstraintViolation<PartLineItemRequest>> violations =
                validator.validate(new PartLineItemRequest(maxName, 1, UUID.randomUUID()));
        assertThat(violations).isEmpty();
    }

    @Test
    void testNegativeQuantity() {
        final Set<ConstraintViolation<PartLineItemRequest>> violations =
                validator.validate(new PartLineItemRequest("Oil Filter", -1, UUID.randomUUID()));
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("must be greater than or equal to 1");
    }
}
