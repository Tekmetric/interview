package com.interview.runningevents.infrastructure.web.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class RunningEventRequestDTOTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    public void shouldCreateValidDTO() {
        // Given
        Long futureTime = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        RunningEventRequestDTO dto = RunningEventRequestDTO.builder()
                .name("Test Marathon")
                .dateTime(futureTime)
                .location("Test Location")
                .description("Test Description")
                .furtherInformation("Further Information")
                .build();

        // When
        Set<ConstraintViolation<RunningEventRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
        assertThat(dto.getName()).isEqualTo("Test Marathon");
        assertThat(dto.getDateTime()).isEqualTo(futureTime);
        assertThat(dto.getLocation()).isEqualTo("Test Location");
        assertThat(dto.getDescription()).isEqualTo("Test Description");
        assertThat(dto.getFurtherInformation()).isEqualTo("Further Information");
    }

    @Test
    public void shouldFailValidationWithMissingRequiredFields() {
        // Given
        RunningEventRequestDTO dto = new RunningEventRequestDTO();

        // When
        Set<ConstraintViolation<RunningEventRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(3);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("name")
                        && v.getMessage().equals("Name is required"));
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("dateTime")
                        && v.getMessage().equals("Date and time is required"));
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("location")
                        && v.getMessage().equals("Location is required"));
    }

    @Test
    public void shouldFailValidationWithTooLongFields() {
        // Given
        Long futureTime = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        RunningEventRequestDTO dto = RunningEventRequestDTO.builder()
                .name("A".repeat(101)) // Too long name
                .dateTime(futureTime)
                .location("B".repeat(256)) // Too long location
                .description("C".repeat(1001)) // Too long description
                .furtherInformation("D".repeat(1001)) // Too long further information
                .build();

        // When
        Set<ConstraintViolation<RunningEventRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(4);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("name")
                        && v.getMessage().contains("100 characters"));
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("location")
                        && v.getMessage().contains("255 characters"));
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("description")
                        && v.getMessage().contains("1000 characters"));
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("furtherInformation")
                        && v.getMessage().contains("1000 characters"));
    }

    @Test
    public void shouldWorkWithEmptyOptionalFields() {
        // Given
        Long futureTime = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        RunningEventRequestDTO dto = RunningEventRequestDTO.builder()
                .name("Test Marathon")
                .dateTime(futureTime)
                .location("Test Location")
                // No description or furtherInformation
                .build();

        // When
        Set<ConstraintViolation<RunningEventRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getFurtherInformation()).isNull();
    }
}
