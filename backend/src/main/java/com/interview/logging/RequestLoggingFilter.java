package com.interview.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final String CORRELATION_HEADER = "X-Correlation-Id";
    private static final String MDC_CORRELATION_KEY = "correlationId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String correlationId = Optional.ofNullable(request.getHeader(CORRELATION_HEADER))
                .map(String::trim)
                .filter(v -> !v.isEmpty())
                .orElse(UUID.randomUUID().toString());

        MDC.put(MDC_CORRELATION_KEY, correlationId);
        response.setHeader(CORRELATION_HEADER, correlationId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (authentication == null) ? null : authentication.getName();

        long startNs = System.nanoTime();
        try {
            log.info("request_start method={} path={} user={}", request.getMethod(), request.getRequestURI(), username);
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = (System.nanoTime() - startNs) / 1_000_000;
            log.info(
                    "request_end method={} path={} user={} status={} durationMs={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    username,
                    response.getStatus(),
                    durationMs
            );
            MDC.clear();
        }
    }
}
