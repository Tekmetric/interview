package com.interview.runningevents.infrastructure.web.validation;

import com.interview.runningevents.application.exception.ValidationException;
import com.interview.runningevents.infrastructure.web.util.DateTimeConverter;

/**
 * Utility class for validating date-related input in the web layer.
 */
public class DateValidator {

    /**
     * Validates if a date string is in the correct format and represents a future date.
     *
     * @param dateString The date string to validate
     * @throws ValidationException if the date is invalid or in the past
     */
    public static void validateFutureDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new ValidationException("Date and time is required");
        }

        if (!DateTimeConverter.isValidDateFormat(dateString)) {
            throw new ValidationException(
                    "Invalid date format. Expected format: yyyy-MM-ddTHH:mm (e.g. 2025-04-30T14:30)");
        }

        // Convert to timestamp
        Long timestamp = DateTimeConverter.toTimestamp(dateString);

        // Check if date is in the future
        if (timestamp <= System.currentTimeMillis()) {
            throw new ValidationException("Event date must be in the future");
        }
    }

    /**
     * Validates that fromDate is before toDate if both are provided.
     *
     * @param fromDateString The from date string
     * @param toDateString The to date string
     * @throws ValidationException if fromDate is after toDate
     */
    public static void validateDateRange(String fromDateString, String toDateString) {
        if (fromDateString != null
                && !fromDateString.trim().isEmpty()
                && toDateString != null
                && !toDateString.trim().isEmpty()) {

            if (!DateTimeConverter.isValidDateFormat(fromDateString)) {
                throw new ValidationException(
                        "Invalid fromDate format. Expected format: yyyy-MM-ddTHH:mm (e.g. 2025-04-30T14:30)");
            }

            if (!DateTimeConverter.isValidDateFormat(toDateString)) {
                throw new ValidationException(
                        "Invalid toDate format. Expected format: yyyy-MM-ddTHH:mm (e.g. 2025-04-30T14:30)");
            }

            Long fromTimestamp = DateTimeConverter.toTimestamp(fromDateString);
            Long toTimestamp = DateTimeConverter.toTimestamp(toDateString);

            if (fromTimestamp > toTimestamp) {
                throw new ValidationException("From date cannot be after to date");
            }
        }
    }
}
