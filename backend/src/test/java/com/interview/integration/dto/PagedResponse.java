package com.interview.integration.dto;

import java.util.List;

public record PagedResponse<T>(
        List<T> content,
        PageInfo page
) {
    public record PageInfo(
            int size,
            int number,
            long totalElements,
            int totalPages
    ) {
    }
}
