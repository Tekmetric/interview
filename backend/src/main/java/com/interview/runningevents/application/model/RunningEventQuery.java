package com.interview.runningevents.application.model;

import lombok.Builder;
import lombok.Data;

/**
 * Parameter object for filtering and paginating running events.
 */
@Data
@Builder
public class RunningEventQuery {

    /**
     * Minimum date (as Unix timestamp in milliseconds) for filtering events.
     * When specified, only events with dateTime >= fromDate will be returned.
     */
    private Long fromDate;

    /**
     * Maximum date (as Unix timestamp in milliseconds) for filtering events.
     * When specified, only events with dateTime <= toDate will be returned.
     */
    private Long toDate;

    /**
     * The maximum number of events to return.
     * Defaults to 20 if not specified.
     */
    @Builder.Default
    private Integer pageSize = 20;

    /**
     * The page number (0-based) for pagination.
     * Defaults to 0 (first page) if not specified.
     */
    @Builder.Default
    private Integer page = 0;

    /**
     * Field name to order results by.
     * Defaults to "dateTime" if not specified.
     */
    @Builder.Default
    private String sortBy = "dateTime";

    /**
     * Direction to sort results (ASC or DESC).
     * Defaults to ASC if not specified.
     */
    @Builder.Default
    private SortDirection sortDirection = SortDirection.ASC;
}
