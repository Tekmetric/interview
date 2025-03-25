package com.interview.runningevents.infrastructure.web.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Paginated response wrapper")
public class PaginatedResponseDTO<T> {

    /**
     * The collection of items in the current page.
     */
    @Schema(description = "Collection of items in the current page")
    private List<T> items;

    /**
     * The total number of items across all pages.
     */
    @Schema(description = "Total number of items across all pages", example = "42")
    private long totalItems;

    /**
     * The current page number (0-based).
     */
    @Schema(description = "Current page number (0-based)", example = "0")
    private int page;

    /**
     * The number of items per page.
     */
    @Schema(description = "Number of items per page", example = "20")
    private int pageSize;

    /**
     * The total number of pages.
     */
    @Schema(description = "Total number of pages", example = "3")
    private int totalPages;

    /**
     * Whether there is a previous page.
     */
    @Schema(description = "Whether there is a previous page", example = "false")
    private boolean hasPrevious;

    /**
     * Whether there is a next page.
     */
    @Schema(description = "Whether there is a next page", example = "true")
    private boolean hasNext;
}
