package com.interview.runningevents.domain.model;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing a running event.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RunningEvent {

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotNull(message = "Date and time is required")
    private Long dateTime;

    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location must be at most 255 characters")
    private String location;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    @Size(max = 1000, message = "Further information must be at most 1000 characters")
    private String furtherInformation;

    /**
     * Validates business rules for the running event.
     *
     * @return true if the running event passes all business validations
     */
    public boolean isValid() {
        // Basic validation
        if (name == null
                || name.trim().isEmpty()
                || dateTime == null
                || location == null
                || location.trim().isEmpty()) {
            return false;
        }

        // Check string length constraints
        if (name.length() > 100
                || location.length() > 255
                || (description != null && description.length() > 1000)
                || (furtherInformation != null && furtherInformation.length() > 1000)) {
            return false;
        }

        return true;
    }

    /**
     * Converts the dateTime from epoch milliseconds to Instant.
     *
     * @return the event date and time as an Instant
     */
    public Instant getDateTimeAsInstant() {
        return dateTime != null ? Instant.ofEpochMilli(dateTime) : null;
    }
}
