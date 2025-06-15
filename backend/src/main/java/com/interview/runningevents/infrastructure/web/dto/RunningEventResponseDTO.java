package com.interview.runningevents.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for sending running event data in API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Running event response data")
public class RunningEventResponseDTO {

    @Schema(description = "Unique identifier of the running event", example = "1")
    private Long id;

    @Schema(description = "Name of the running event", example = "Spring Marathon 2025")
    private String name;

    @Schema(description = "Date and time of the event in format yyyy-MM-ddTHH:mm", example = "2025-04-30T14:30")
    private String dateTime;

    @Schema(description = "Location where the event will take place", example = "Central Park, New York")
    private String location;

    @Schema(
            description = "Description of the event",
            example = "Annual spring marathon through the scenic Central Park. Open to runners of all levels.")
    private String description;

    @Schema(
            description = "Additional information about the event",
            example = "Water stations every 2 miles. Registration closes 2 weeks before the event.")
    private String furtherInformation;
}
