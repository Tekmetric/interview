package com.interview.config;

import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * This is a Servlet Filter to demonstrate adding a unique trace ID to each request for logging purposes.
 * This makes it easier to correlate logs for debugging and tracing through distributed systems.
 *
 * It utilizes MDC which is a best practice for logging context in Java apps. If other systems were involved
 * in this demo and were passing along trace IDs, we would extract and re-use those here from the MDC headers.
 */
@Component
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            String traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
            chain.doFilter(request, response);
        } finally {
            MDC.remove("traceId");
        }
    }
}
