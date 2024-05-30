package com.interview.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class HttpLinkHeaderUriBuilderTest {

    @Autowired
    private HttpLinkHeaderUriBuilder httpLinkHeaderUriBuilder;

    @Mock
    private Page<?> page;

    @BeforeEach
    void setUp() {
        when(page.getNumber()).thenReturn(2);
        when(page.getSize()).thenReturn(10);
    }

    @Test
    void givenPageWithPrevious_whenBuildHttpLinkHeaderForPage_thenReturnCorrectHeaderLink() {
        when(page.hasPrevious()).thenReturn(true);
        when(page.hasNext()).thenReturn(false);
        var actualHttpHeaders = httpLinkHeaderUriBuilder.buildHttpLinkHeaderForPage(page);
        assertThat(actualHttpHeaders).containsEntry(HttpHeaders.LINK, List.of("<http://localhost:8080/api/shops/page?number=1&size=10>; rel=\"prev\""));
    }

    @Test
    void givenPageWithNext_whenBuildHttpLinkHeaderForPage_thenReturnCorrectHeaderLink() {
        when(page.hasPrevious()).thenReturn(false);
        when(page.hasNext()).thenReturn(true);
        var actualHttpHeaders = httpLinkHeaderUriBuilder.buildHttpLinkHeaderForPage(page);
        assertThat(actualHttpHeaders).containsEntry(HttpHeaders.LINK, List.of("<http://localhost:8080/api/shops/page?number=3&size=10>; rel=\"next\""));
    }

    @Test
    void givenPageWithPreviousAndNext_whenBuildHttpLinkHeaderForPage_thenReturnCorrectHeaderLink() {
        when(page.hasPrevious()).thenReturn(true);
        when(page.hasNext()).thenReturn(true);
        var actualHttpHeaders = httpLinkHeaderUriBuilder.buildHttpLinkHeaderForPage(page);
        assertThat(actualHttpHeaders).containsEntry(HttpHeaders.LINK, List.of("<http://localhost:8080/api/shops/page?number=1&size=10>; rel=\"prev\",<http://localhost:8080/api/shops/page?number=3&size=10>; rel=\"next\""));
    }
}
