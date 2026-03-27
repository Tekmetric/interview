package com.interview.test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public class Page<T> extends PageImpl<T> {

    @JsonCreator
    public Page(@JsonProperty("content") List<T> content,
                @JsonProperty("page") PageMetadata page) {
        super(content, PageRequest.of(page.number(), page.size()), page.totalElements());
    }

    public record PageMetadata(
        @JsonProperty("size") int size,
        @JsonProperty("number")  int number,
        @JsonProperty("totalElements")  long totalElements,
        @JsonProperty("totalPages")  int totalPages
    ) {
    }

    @JsonProperty("page")
    public PageMetadata getPageMetadata() {
        return new PageMetadata(
            getSize(),
            getNumber(),
            getTotalElements(),
            getTotalPages()
        );
    }
}