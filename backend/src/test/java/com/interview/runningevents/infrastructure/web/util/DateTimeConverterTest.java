package com.interview.runningevents.infrastructure.web.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DateTimeConverterTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    @Test
    void shouldConvertStringToTimestamp() {
        // Given
        String dateTimeString = "2025-04-29T10:00";

        // When
        Long timestamp = DateTimeConverter.toTimestamp(dateTimeString);

        // Then
        LocalDateTime expectedDateTime = LocalDateTime.parse(dateTimeString, FORMATTER);
        Long expectedTimestamp = expectedDateTime.atZone(ZONE_ID).toInstant().toEpochMilli();

        assertThat(timestamp).isEqualTo(expectedTimestamp);
    }

    @Test
    void shouldConvertTimestampToString() {
        // Given
        String dateTimeString = "2025-04-29T10:00";
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, FORMATTER);
        Long timestamp = dateTime.atZone(ZONE_ID).toInstant().toEpochMilli();

        // When
        String result = DateTimeConverter.fromTimestamp(timestamp);

        // Then
        assertThat(result).isEqualTo(dateTimeString);
    }

    @Test
    void shouldHandleNullValuesGracefully() {
        // When/Then
        assertThat(DateTimeConverter.toTimestamp(null)).isNull();
        assertThat(DateTimeConverter.fromTimestamp(null)).isNull();
    }

    @Test
    void shouldHandleEmptyStringGracefully() {
        // When/Then
        assertThat(DateTimeConverter.toTimestamp("")).isNull();
        assertThat(DateTimeConverter.toTimestamp("   ")).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"2025-04-29T10:00", "2030-12-31T23:59", "2025-01-01T00:00"})
    void shouldValidateDateFormat(String validDate) {
        // When/Then
        assertThat(DateTimeConverter.isValidDateFormat(validDate)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "2025-04-29", // Missing time
                "10:00", // Missing date
                "2025/04/29T10:00", // Wrong format
                "29-04-2025T10:00", // Wrong order
                "2025-04-29T10:00:00", // Includes seconds
                "2025-04-29 10:00", // Space instead of T
                "2025-13-29T10:00", // Invalid month
                "2025-04-32T10:00", // Invalid day
                "2025-04-29T25:00", // Invalid hour
                "abcdefg"
            })
    void shouldInvalidateBadDateFormat(String invalidDate) {
        // When/Then
        assertThat(DateTimeConverter.isValidDateFormat(invalidDate)).isFalse();
    }

    @Test
    void shouldThrowExceptionForInvalidFormat() {
        // Given
        String invalidDate = "2025/04/29T10:00"; // Wrong format

        // When/Then
        assertThatThrownBy(() -> DateTimeConverter.toTimestamp(invalidDate))
                .isInstanceOf(DateTimeParseException.class)
                .hasMessageContaining("Invalid date format");
    }

    @Test
    void shouldRoundTripAccurately() {
        // Given - current time rounded to minutes
        Instant now = Instant.now().truncatedTo(ChronoUnit.MINUTES);
        Long timestamp = now.toEpochMilli();

        // When
        String dateString = DateTimeConverter.fromTimestamp(timestamp);
        Long roundTrippedTimestamp = DateTimeConverter.toTimestamp(dateString);

        // Then - there might be slight differences due to millisecond truncation
        long differenceInMinutes = Math.abs((timestamp - roundTrippedTimestamp) / (1000 * 60));

        // Should be within a minute of the original timestamp
        assertThat(differenceInMinutes).isLessThanOrEqualTo(1);
    }
}
