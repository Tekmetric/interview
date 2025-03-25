package com.interview.runningevents.infrastructure.web.dto;

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
public class RunningEventRequestDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotBlank(message = "Date and time is required")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$",
            message = "Date must be in the format yyyy-MM-ddTHH:mm (e.g. 2025-04-30T14:30)")
    private String dateTime;

    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location must be at most 255 characters")
    private String location;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    @Size(max = 1000, message = "Further information must be at most 1000 characters")
    private String furtherInformation;
}
