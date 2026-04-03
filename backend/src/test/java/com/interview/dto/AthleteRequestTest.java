package com.interview.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AthleteRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidAthleteRequest() {
        AthleteRequest request = new AthleteRequest(
                "John", "Doe", LocalDate.of(1990, 1, 1), "USA", "100m Sprint", "9.99s", "Fast runner");

        Set<ConstraintViolation<AthleteRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenFirstNameIsBlank() {
        AthleteRequest request =
                new AthleteRequest("", "Doe", LocalDate.of(1990, 1, 1), "USA", "100m Sprint", "9.99s", "Fast runner");

        Set<ConstraintViolation<AthleteRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("First name is required");
    }

    @Test
    void shouldFailWhenLastNameIsBlank() {
        AthleteRequest request =
                new AthleteRequest("John", "", LocalDate.of(1990, 1, 1), "USA", "100m Sprint", "9.99s", "Fast runner");

        Set<ConstraintViolation<AthleteRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Last name is required");
    }

    @Test
    void shouldFailWhenBirthDateIsNull() {
        AthleteRequest request = new AthleteRequest("John", "Doe", null, "USA", "100m Sprint", "9.99s", "Fast runner");

        Set<ConstraintViolation<AthleteRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Birth date is required");
    }

    @Test
    void shouldFailWhenBirthDateIsInFuture() {
        AthleteRequest request = new AthleteRequest(
                "John", "Doe", LocalDate.now().plusDays(1), "USA", "100m Sprint", "9.99s", "Fast runner");

        Set<ConstraintViolation<AthleteRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Birth date must be in the past or present");
    }

    @Test
    void shouldAllowTodayAsBirthDate() {
        AthleteRequest request =
                new AthleteRequest("John", "Doe", LocalDate.now(), "USA", "100m Sprint", "9.99s", "Fast runner");

        Set<ConstraintViolation<AthleteRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenFirstNameExceedsMaxLength() {
        AthleteRequest request = new AthleteRequest(
                "ThisNameIsWayTooLongAndExceedsTheThirtyCharacterLimit",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "USA",
                "100m Sprint",
                "9.99s",
                "Fast runner");

        Set<ConstraintViolation<AthleteRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("First name must not exceed 30 characters");
    }

    @Test
    void shouldAllowNullPersonalBest() {
        AthleteRequest request =
                new AthleteRequest("John", "Doe", LocalDate.of(1990, 1, 1), "USA", "100m Sprint", null, "Bio");

        Set<ConstraintViolation<AthleteRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldAllowNullBio() {
        AthleteRequest request =
                new AthleteRequest("John", "Doe", LocalDate.of(1990, 1, 1), "USA", "100m Sprint", "9.99s", null);

        Set<ConstraintViolation<AthleteRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }
}
