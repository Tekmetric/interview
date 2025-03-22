package com.interview.runningevents.application.port.in;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * A wrapper for paginated results that includes the data items and pagination metadata.
 *
 * @param <T> The type of elements in the result list
 */
@Data
@AllArgsConstructor
@Builder
public class PaginatedResult<T> {

    /**
     * The data items in the current page.
     */
    private final List<T> items;

    /**
     * The total number of items across all pages.
     */
    private final long totalItems;

    /**
     * The current page number (0-based).
     */
    private final int page;

    /**
     * The number of items per page.
     */
    private final int pageSize;

    /**
     * The total number of pages.
     */
    private final int totalPages;

    /**
     * Whether there is a previous page.
     */
    private final boolean hasPrevious;

    /**
     * Whether there is a next page.
     */
    private final boolean hasNext;

    /**
     * Creates an empty paginated result.
     *
     * @param <T> The type of elements
     * @return An empty paginated result
     */
    public static <T> PaginatedResult<T> empty() {
        return new PaginatedResult<>(Collections.emptyList(), 0, 0, 0, 0, false, false);
    }

    /**
     * Creates a paginated result with the given items and pagination information.
     *
     * @param <T> The type of elements
     * @param items The list of items
     * @param totalItems The total number of items
     * @param page The current page number
     * @param pageSize The page size
     * @return A new paginated result
     */
    public static <T> PaginatedResult<T> of(List<T> items, long totalItems, int page, int pageSize) {
        int totalPages = pageSize > 0 ? (int) Math.ceil((double) totalItems / pageSize) : 0;
        boolean hasPrevious = page > 0;
        boolean hasNext = page < totalPages - 1;

        return new PaginatedResult<>(items, totalItems, page, pageSize, totalPages, hasPrevious, hasNext);
    }
}
