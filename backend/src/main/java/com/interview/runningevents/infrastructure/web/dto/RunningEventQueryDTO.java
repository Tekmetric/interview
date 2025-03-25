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
     * Minimum date in format yyyy-MM-dd HH:mm for filtering events.
     */
    private String fromDate;

    /**
     * Maximum date in format yyyy-MM-dd HH:mm for filtering events.
     */
    private String toDate;

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
     * This is a string in the DTO and will be converted to the enum in the service layer.
     */
    @Builder.Default
    private String sortDirection = "ASC";
}
