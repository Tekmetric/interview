package com.interview.runningevents.infrastructure.web.dto;

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
public class RunningEventResponseDTO {

    private Long id;
    private String name;
    private String dateTime;
    private String location;
    private String description;
    private String furtherInformation;
}
