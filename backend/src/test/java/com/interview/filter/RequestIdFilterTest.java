package com.interview.filter;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class RequestIdFilterTest {

    private final RequestIdFilter filter = new RequestIdFilter();

    @Test
    void echoesProvidedRequestIdHeader() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader(RequestIdFilter.HEADER, "abc-123");
        MockHttpServletResponse resp = new MockHttpServletResponse();
        FilterChain chain = (r, s) ->
                assertThat(MDC.get(RequestIdFilter.MDC_KEY)).isEqualTo("abc-123");

        filter.doFilter(req, resp, chain);

        assertThat(resp.getHeader(RequestIdFilter.HEADER)).isEqualTo("abc-123");
        assertThat(MDC.get(RequestIdFilter.MDC_KEY)).isNull();
    }

    @Test
    void generatesRequestIdWhenAbsent() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse resp = new MockHttpServletResponse();
        FilterChain chain = (r, s) ->
                assertThat(MDC.get(RequestIdFilter.MDC_KEY)).isNotBlank();

        filter.doFilter(req, resp, chain);

        assertThat(resp.getHeader(RequestIdFilter.HEADER)).isNotBlank();
    }
}
