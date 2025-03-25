package com.interview.runningevents.infrastructure.web.util;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for converting between formatted date strings and Unix timestamps.
 */
public class DateTimeConverter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    /**
     * Converts a formatted date string to Unix timestamp in milliseconds.
     *
     * @param dateTimeString The date string in format "yyyy-MM-ddTHH:mm"
     * @return Unix timestamp in milliseconds
     * @throws DateTimeParseException if the string cannot be parsed
     */
    public static Long toTimestamp(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }

        try {
            LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, FORMATTER);
            return localDateTime.atZone(ZONE_ID).toInstant().toEpochMilli();
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException(
                    "Invalid date format. Expected format: yyyy-MM-ddTHH:mm (e.g. 2025-04-30T14:30)",
                    dateTimeString,
                    e.getErrorIndex());
        }
    }

    /**
     * Converts a Unix timestamp in milliseconds to a formatted date string.
     *
     * @param timestamp Unix timestamp in milliseconds
     * @return Formatted date string in the format "yyyy-MM-ddTHH:mm" or null if timestamp is null
     */
    public static String fromTimestamp(Long timestamp) {
        if (timestamp == null) {
            return null;
        }

        try {
            return FORMATTER.format(java.time.Instant.ofEpochMilli(timestamp).atZone(ZONE_ID));
        } catch (DateTimeException e) {
            return null;
        }
    }

    /**
     * Validates if a string is in the correct date format.
     *
     * @param dateTimeString The date string to validate
     * @return true if the string is a valid date in the expected format, false otherwise
     */
    public static boolean isValidDateFormat(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return false;
        }

        try {
            LocalDateTime.parse(dateTimeString, FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
