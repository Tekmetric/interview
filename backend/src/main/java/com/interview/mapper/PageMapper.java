package com.interview.mapper;

import com.interview.dto.PagedResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface PageMapper {
    // Generic mapper for pagination usage
    default <T> PagedResponse<T> toPagedResponse(Page<T> page) {
        return new PagedResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }
}