package com.interview.runningevents.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Query parameters for filtering and pagination of running events")
public class RunningEventQueryDTO {

    /**
     * Minimum date in format yyyy-MM-dd HH:mm for filtering events.
     */
    @Schema(description = "Minimum date in format yyyy-MM-ddTHH:mm for filtering events", example = "2025-01-01T00:00")
    private String fromDate;

    /**
     * Maximum date in format yyyy-MM-dd HH:mm for filtering events.
     */
    @Schema(description = "Maximum date in format yyyy-MM-ddTHH:mm for filtering events", example = "2025-12-31T23:59")
    private String toDate;

    /**
     * The page number (0-based) for pagination.
     */
    @Builder.Default
    @Schema(description = "Page number (0-based) for pagination", example = "0", defaultValue = "0")
    private Integer page = 0;

    /**
     * The number of items per page.
     */
    @Builder.Default
    @Schema(description = "Number of items per page", example = "20", defaultValue = "20")
    private Integer pageSize = 20;

    /**
     * Field to sort by (e.g., "dateTime", "name").
     */
    @Builder.Default
    @Schema(
            description = "Field to sort by",
            example = "dateTime",
            allowableValues = {"dateTime", "name", "id"},
            defaultValue = "dateTime")
    private String sortBy = "dateTime";

    /**
     * Sort direction (ASC or DESC).
     * This is a string in the DTO and will be converted to the enum in the service layer.
     */
    @Builder.Default
    @Schema(
            description = "Sort direction",
            example = "ASC",
            allowableValues = {"ASC", "DESC"},
            defaultValue = "ASC")
    private String sortDirection = "ASC";
}
