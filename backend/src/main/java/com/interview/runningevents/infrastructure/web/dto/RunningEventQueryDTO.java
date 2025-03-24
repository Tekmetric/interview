package com.interview.runningevents.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for receiving query parameters for listing running events.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RunningEventQueryDTO {

    /**
     * Minimum date (Unix timestamp in milliseconds) for filtering events.
     */
    private Long fromDate;

    /**
     * Maximum date (Unix timestamp in milliseconds) for filtering events.
     */
    private Long toDate;

    /**
     * The page number (0-based) for pagination.
     */
    @Builder.Default
    private Integer page = 0;

    /**
     * The number of items per page.
     */
    @Builder.Default
    private Integer pageSize = 20;

    /**
     * Field to sort by (e.g., "dateTime", "name").
     */
    @Builder.Default
    private String sortBy = "dateTime";

    /**
     * Sort direction (ASC or DESC).
     */
    @Builder.Default
    private String sortDirection = "ASC";
}
