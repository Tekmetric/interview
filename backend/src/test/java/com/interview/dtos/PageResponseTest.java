package com.interview.dtos;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PageResponseTest {

    @Test
    void testFromPage() {
        int pageSize = 10;
        int pageNumber = 1;
        long totalElements = 35;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<String> content = IntStream.range(11, 21)
                .mapToObj(String::valueOf)
                .collect(Collectors.toList());

        Page<String> springPage = new PageImpl<>(content, pageable, totalElements);

        PageResponse<String> pageResponse = PageResponse.from(springPage);

        assertNotNull(pageResponse);

        assertEquals(content, pageResponse.content());

        PageResponse.PageMetadata metadata = pageResponse.page();
        assertNotNull(metadata);
        assertEquals(pageSize, metadata.size());
        assertEquals(pageNumber, metadata.number());
        assertEquals(totalElements, metadata.totalElements());
        assertEquals(4, metadata.totalPages());
    }
}
