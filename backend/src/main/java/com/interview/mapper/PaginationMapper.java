package com.interview.mapper;

import com.interview.dto.search.PageRequestDto;
import com.interview.dto.search.PageResponseDto;
import com.interview.dto.search.SortBy;
import com.interview.exception.ErrorCode;
import com.interview.exception.InternalServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.function.Function;

import static com.interview.dto.search.Direction.ASC;
import static com.interview.dto.search.Direction.DESC;

public class PaginationMapper {

    public static PageRequest toPageRequest(PageRequestDto pageRequestDto) {
        if (CollectionUtils.isEmpty(pageRequestDto.sortBy())){
            return PageRequest.of(pageRequestDto.pageNumber(), pageRequestDto.pageSize());
        }

        return PageRequest.of(pageRequestDto.pageNumber(), pageRequestDto.pageSize(), getSort(pageRequestDto.sortBy()));
    }

    public static <E, T> PageResponseDto<T> toPageResponse(Page<E> page, Function<E, T> mappingFunction) {
        return PageResponseDto.<T>builder()
                .pageNumber(page.getNumber())
                .pageSize(page.getContent().size())
                .content(page.getContent().stream()
                        .map(mappingFunction)
                        .toList())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    private static Sort getSort(List<SortBy> sortByes) {
        final List<Sort.Order> orders = sortByes.stream()
                .map(PaginationMapper::getOrder)
                .toList();

        return Sort.by(orders);
    }

    private static Sort.Order getOrder(SortBy sortBy) {
        if (ASC.equals(sortBy.direction())) {
            return Sort.Order.asc(sortBy.fieldName().getColumnName());
        } else if (DESC.equals(sortBy.direction())) {
            return Sort.Order.desc(sortBy.fieldName().getColumnName());
        }
        throw new InternalServiceException(ErrorCode.INTERNAL_SERVER_ERROR,
                "Unknown sort direction: %s".formatted(sortBy.direction()));
    }

}
