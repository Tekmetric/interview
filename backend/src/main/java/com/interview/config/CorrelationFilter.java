package com.interview.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Filter to handle correlation IDs for request tracing.
 *
 * <p>Extracts correlation ID from request header or generates a new one.
 * Stores it in MDC for automatic inclusion in logs and returns it in response headers.
 * Ensures every request can be traced through the system.
 */
@Slf4j
@Component
public class CorrelationFilter implements Filter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            // Extract or generate correlation ID
            String correlationId = extractOrGenerateCorrelationId(httpRequest);

            // Store in MDC for logging
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId);

            // Add to response header so client can use it
            httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId);

            log.debug("Processing request with correlation ID: {}", correlationId);

            // Continue with request processing
            chain.doFilter(request, response);

        } finally {
            // Always clean up MDC to prevent memory leaks
            MDC.clear();
        }
    }

    /**
     * Extract correlation ID from request header or generate new one.
     */
    private String extractOrGenerateCorrelationId(HttpServletRequest request) {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);

        if (correlationId == null || correlationId.trim().isEmpty()) {
            // Generate new correlation ID
            correlationId = "req-" + UUID.randomUUID().toString().substring(0, 8);
            log.debug("Generated new correlation ID: {}", correlationId);
        } else {
            log.debug("Using correlation ID from header: {}", correlationId);
        }

        return correlationId;
    }

    /**
     * Disable auto-registration of this filter by Spring Boot.
     * We manually register it in SecurityConfig for proper ordering.
     */
    @Bean
    public FilterRegistrationBean<CorrelationFilter> correlationFilterRegistration() {
        FilterRegistrationBean<CorrelationFilter> registration = new FilterRegistrationBean<>(this);
        registration.setEnabled(false); // Disable auto-registration
        return registration;
    }
}