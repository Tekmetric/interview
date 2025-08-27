package com.interview.dto.search;

import lombok.Builder;

import java.util.List;

@Builder
public record PageResponseDto<T>(int pageNumber,
                                 int pageSize,
                                 List<T> content,
                                 long totalElements,
                                 int totalPages) {
}
