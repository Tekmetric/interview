package com.interview.dtos;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
@Schema(description = "A generic paginated response.")
public record PageResponse<T>(
        @ArraySchema(arraySchema = @Schema(description = "The list of items on the current page."))
        List<T> content,

        @Schema(description = "Pagination metadata.")
        PageMetadata page
) {

    @Builder
    @Schema(description = "Pagination metadata.")
    public record PageMetadata(
            @Schema(description = "The number of items per page.")
            int size,

            @Schema(description = "The current page number (0-indexed).")
            int number,

            @Schema(description = "The total number of items available across all pages.")
            long totalElements,

            @Schema(description = "The total number of pages available.")
            int totalPages
    ) {
    }

    public static <T> PageResponse<T> from(Page<T> page) {
        PageMetadata metadata = PageMetadata.builder()
                .size(page.getSize())
                .number(page.getNumber())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();

        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(metadata)
                .build();
    }
}
