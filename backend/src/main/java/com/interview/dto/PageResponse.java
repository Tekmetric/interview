package com.interview.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first, // true when current page is the first page
        boolean last   // true when current page is the last page
) {}
