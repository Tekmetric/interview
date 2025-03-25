package com.interview.runningevents.application.model;

import java.util.Arrays;

/**
 * Enum for representing sort direction options.
 */
public enum SortDirection {
    ASC,
    DESC;

    /**
     * Parse a string value to a SortDirection enum.
     *
     * @param value The string value to parse (case-insensitive)
     * @return The matching SortDirection or ASC if not valid
     */
    public static SortDirection fromString(String value) {
        if (value == null) {
            return ASC;
        }

        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ASC;
        }
    }

    /**
     * Validates if a string value is a valid sort direction.
     *
     * @param value The string value to validate
     * @return true if it's a valid sort direction, false otherwise
     */
    public static boolean isValid(String value) {
        if (value == null) {
            return false;
        }

        return Arrays.stream(values()).anyMatch(direction -> direction.name().equalsIgnoreCase(value));
    }
}
