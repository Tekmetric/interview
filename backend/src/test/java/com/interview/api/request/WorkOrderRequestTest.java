package com.interview.api.request;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class WorkOrderRequestTest {

    private final Validator validator;

    WorkOrderRequestTest() {
        try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    void testHappyPath() {
        final Set<ConstraintViolation<WorkOrderRequest>> violations = validator.validate(
                new WorkOrderRequest(Instant.parse("2026-04-01T09:00:00Z"), UUID.randomUUID(), UUID.randomUUID()));
        assertThat(violations).isEmpty();
    }

    @Test
    void testNullScheduledStartDateTime() {
        final Set<ConstraintViolation<WorkOrderRequest>> violations =
                validator.validate(new WorkOrderRequest(null, UUID.randomUUID(), UUID.randomUUID()));
        assertThat(violations).extracting(ConstraintViolation::getMessage).contains("must not be null");
    }

    @Test
    void testNullCustomerId() {
        final Set<ConstraintViolation<WorkOrderRequest>> violations = validator.validate(
                new WorkOrderRequest(Instant.parse("2026-04-01T09:00:00Z"), null, UUID.randomUUID()));
        assertThat(violations).extracting(ConstraintViolation::getMessage).contains("must not be null");
    }

    @Test
    void testNullVehicleId() {
        final Set<ConstraintViolation<WorkOrderRequest>> violations = validator.validate(
                new WorkOrderRequest(Instant.parse("2026-04-01T09:00:00Z"), UUID.randomUUID(), null));
        assertThat(violations).extracting(ConstraintViolation::getMessage).contains("must not be null");
    }
}
