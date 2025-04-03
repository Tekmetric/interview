package com.interview.runningevents.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class RunningEventTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void shouldCreateRunningEventWithAllFields() {
        // Given
        Long futureTime = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        // When
        RunningEvent event = RunningEvent.builder()
                .id(1L)
                .name("Test Marathon")
                .dateTime(futureTime)
                .location("Test Location")
                .description("Test Description")
                .furtherInformation("Further Test Information")
                .build();

        // Then
        assertNotNull(event);
        assertEquals(1L, event.getId());
        assertEquals("Test Marathon", event.getName());
        assertEquals(futureTime, event.getDateTime());
        assertEquals("Test Location", event.getLocation());
        assertEquals("Test Description", event.getDescription());
        assertEquals("Further Test Information", event.getFurtherInformation());
        assertTrue(event.isValid());
    }

    @Test
    void shouldCreateRunningEventWithRequiredFieldsOnly() {
        // Given
        Long futureTime = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();

        // When
        RunningEvent event = RunningEvent.builder()
                .name("Test Marathon")
                .dateTime(futureTime)
                .location("Test Location")
                .build();

        // Then
        assertNotNull(event);
        assertEquals("Test Marathon", event.getName());
        assertEquals(futureTime, event.getDateTime());
        assertEquals("Test Location", event.getLocation());
        assertNull(event.getDescription());
        assertNull(event.getFurtherInformation());
        assertTrue(event.isValid());
    }

    @Test
    void shouldConvertDateTimeToInstant() {
        // Given
        Instant now = Instant.now();
        RunningEvent event = RunningEvent.builder().dateTime(now.toEpochMilli()).build();

        // When
        Instant result = event.getDateTimeAsInstant();

        // Then
        // We use truncatedTo(ChronoUnit.MILLIS) to handle potential nanosecond differences
        assertEquals(now.truncatedTo(ChronoUnit.MILLIS), result.truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    void shouldFailJakartaValidationForMissingRequiredFields() {
        // Given
        RunningEvent event = new RunningEvent();

        // When
        Set<ConstraintViolation<RunningEvent>> violations = validator.validate(event);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(3, violations.size()); // name, dateTime, location

        // Check specific validation messages
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name")
                        && v.getMessage().equals("Name is required")));

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("dateTime")
                        && v.getMessage().equals("Date and time is required")));

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("location")
                        && v.getMessage().equals("Location is required")));
    }

    @ParameterizedTest
    @MethodSource("invalidEventProvider")
    void shouldFailValidationForInvalidEvents(
            String name, Long dateTime, String location, String description, String furtherInformation) {
        // Given
        RunningEvent event = RunningEvent.builder()
                .name(name)
                .dateTime(dateTime)
                .location(location)
                .description(description)
                .furtherInformation(furtherInformation)
                .build();

        // Then
        assertFalse(event.isValid());
    }

    private static Stream<Arguments> invalidEventProvider() {
        Long futureTime = Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli();
        Long pastTime = Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli();

        return Stream.of(
                // Name validation
                Arguments.of(null, futureTime, "Location", null, null),
                Arguments.of("", futureTime, "Location", null, null),
                Arguments.of("  ", futureTime, "Location", null, null),
                Arguments.of("x".repeat(101), futureTime, "Location", null, null),

                // DateTime validation
                Arguments.of("Name", null, "Location", null, null),

                // Location validation
                Arguments.of("Name", futureTime, null, null, null),
                Arguments.of("Name", futureTime, "", null, null),
                Arguments.of("Name", futureTime, "  ", null, null),
                Arguments.of("Name", futureTime, "x".repeat(256), null, null),

                // Description validation
                Arguments.of("Name", futureTime, "Location", "x".repeat(1001), null),

                // FurtherInformation validation
                Arguments.of("Name", futureTime, "Location", null, "x".repeat(1001)));
    }

    @Test
    void shouldValidateMaxLengthsWithJakartaValidation() {
        // Given
        RunningEvent event = RunningEvent.builder()
                .name("x".repeat(101))
                .dateTime(Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli())
                .location("x".repeat(256))
                .description("x".repeat(1001))
                .furtherInformation("x".repeat(1001))
                .build();

        // When
        Set<ConstraintViolation<RunningEvent>> violations = validator.validate(event);

        // Then
        assertEquals(4, violations.size());

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name")
                        && v.getMessage().contains("at most 100")));

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("location")
                        && v.getMessage().contains("at most 255")));

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("description")
                        && v.getMessage().contains("at most 1000")));

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("furtherInformation")
                        && v.getMessage().contains("at most 1000")));
    }

    @Test
    void shouldSupportNoArgsConstructor() {
        // When
        RunningEvent event = new RunningEvent();

        // Then
        assertNotNull(event);
        assertNull(event.getId());
        assertNull(event.getName());
        assertNull(event.getDateTime());
        assertNull(event.getLocation());
        assertNull(event.getDescription());
        assertNull(event.getFurtherInformation());
    }

    @Test
    void shouldSupportAllArgsConstructor() {
        // When
        RunningEvent event = new RunningEvent(1L, "Name", 123456789L, "Location", "Description", "Further Info");

        // Then
        assertEquals(1L, event.getId());
        assertEquals("Name", event.getName());
        assertEquals(123456789L, event.getDateTime());
        assertEquals("Location", event.getLocation());
        assertEquals("Description", event.getDescription());
        assertEquals("Further Info", event.getFurtherInformation());
    }

    @Test
    void shouldSupportGettersAndSetters() {
        // Given
        RunningEvent event = new RunningEvent();

        // When
        event.setId(1L);
        event.setName("Updated Name");
        event.setDateTime(987654321L);
        event.setLocation("Updated Location");
        event.setDescription("Updated Description");
        event.setFurtherInformation("Updated Further Info");

        // Then
        assertEquals(1L, event.getId());
        assertEquals("Updated Name", event.getName());
        assertEquals(987654321L, event.getDateTime());
        assertEquals("Updated Location", event.getLocation());
        assertEquals("Updated Description", event.getDescription());
        assertEquals("Updated Further Info", event.getFurtherInformation());
    }
}
