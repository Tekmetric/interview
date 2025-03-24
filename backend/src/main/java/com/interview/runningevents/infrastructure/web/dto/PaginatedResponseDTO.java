package com.interview.runningevents.infrastructure.web.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic DTO for paginated responses.
 *
 * @param <T> The type of items in the paginated result
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginatedResponseDTO<T> {

    /**
     * The collection of items in the current page.
     */
    private List<T> items;

    /**
     * The total number of items across all pages.
     */
    private long totalItems;

    /**
     * The current page number (0-based).
     */
    private int page;

    /**
     * The number of items per page.
     */
    private int pageSize;

    /**
     * The total number of pages.
     */
    private int totalPages;

    /**
     * Whether there is a previous page.
     */
    private boolean hasPrevious;

    /**
     * Whether there is a next page.
     */
    private boolean hasNext;
}
