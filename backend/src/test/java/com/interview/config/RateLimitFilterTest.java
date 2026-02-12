package com.interview.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests for RateLimitFilter to ensure proper rate limiting behavior.
 */
@ExtendWith(MockitoExtension.class)
class RateLimitFilterTest {

    @Mock
    private Bucket rateLimitBucket;

    private RateLimitFilter rateLimitFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        rateLimitFilter = new RateLimitFilter(rateLimitBucket);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void shouldAllowRequestWhenBucketHasTokens() throws Exception {
        // Given
        when(rateLimitBucket.tryConsume(1)).thenReturn(true);
        MockFilterChain filterChain = new MockFilterChain();

        // When
        rateLimitFilter.doFilterInternal(request, response, filterChain);

        // Then - filter chain should be called (status 200 means request went through)
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(filterChain.getRequest()).isNotNull();
    }

    @Test
    void shouldBlockRequestWhenBucketHasNoTokens() throws Exception {
        // Given
        when(rateLimitBucket.tryConsume(1)).thenReturn(false);
        MockFilterChain filterChain = new MockFilterChain();

        // When
        rateLimitFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(response.getStatus()).isEqualTo(429); // TOO_MANY_REQUESTS
        assertThat(response.getContentAsString()).isEqualTo("Too many requests");
        assertThat(filterChain.getRequest()).isNull(); // Filter chain was not called
    }
}
