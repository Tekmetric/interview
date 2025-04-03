package com.interview.runningevents.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for receiving running event data from API requests.
 * Used for both creation and update operations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Running event request data")
public class RunningEventRequestDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    @Schema(description = "Name of the running event", example = "Spring Marathon 2025", required = true)
    private String name;

    @NotBlank(message = "Date and time is required")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$",
            message = "Date must be in the format yyyy-MM-ddTHH:mm (e.g. 2025-04-30T14:30)")
    @Schema(
            description = "Date and time of the event in format yyyy-MM-ddTHH:mm",
            example = "2025-04-30T14:30",
            required = true)
    private String dateTime;

    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location must be at most 255 characters")
    @Schema(
            description = "Location where the event will take place",
            example = "Central Park, New York",
            required = true)
    private String location;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    @Schema(
            description = "Description of the event",
            example = "Annual spring marathon through the scenic Central Park. Open to runners of all levels.")
    private String description;

    @Size(max = 1000, message = "Further information must be at most 1000 characters")
    @Schema(
            description = "Additional information about the event",
            example = "Water stations every 2 miles. Registration closes 2 weeks before the event.")
    private String furtherInformation;
}
