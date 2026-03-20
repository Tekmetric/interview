package com.interview.model.dto;

import java.util.List;

public record VehiclePageResponse(
        List<VehicleResponse> content,
        int currentPage,
        int totalPages,
        long totalElements,
        int pageSize,
        boolean last
) {}