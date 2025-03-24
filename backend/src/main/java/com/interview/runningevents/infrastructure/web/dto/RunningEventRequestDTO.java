package com.interview.runningevents.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Date and time is required")
    private Long dateTime;

    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location must be at most 255 characters")
    private String location;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    @Size(max = 1000, message = "Further information must be at most 1000 characters")
    private String furtherInformation;
}
