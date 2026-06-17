package com.interview.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(name = "PageResponse", description = "Stable paginated response wrapper")
public record PageResponse<T>(
        @Schema(description = "Items for the current page")
        List<T> items,
        @Schema(description = "Zero-based page number", example = "0")
        int page,
        @Schema(description = "Requested page size", example = "20")
        int size,
        @Schema(description = "Number of items in the current page", example = "3")
        int itemCount,
        @Schema(description = "Total number of matching items", example = "42")
        long totalElements,
        @Schema(description = "Total number of pages", example = "3")
        int totalPages,
        @Schema(description = "Whether this is the first page", example = "true")
        boolean first,
        @Schema(description = "Whether this is the last page", example = "false")
        boolean last
) {
    // Use an explicit DTO so the public JSON contract does not depend on Spring Data's PageImpl serialization details.
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
