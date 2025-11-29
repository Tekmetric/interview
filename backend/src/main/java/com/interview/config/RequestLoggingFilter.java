package com.interview.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class RequestLoggingFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(RequestLoggingFilter.class);

    private final RequestLoggingConfig requestLoggingConfig;

    public RequestLoggingFilter(RequestLoggingConfig requestLoggingConfig) {
        this.requestLoggingConfig = requestLoggingConfig;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        if (request instanceof HttpServletRequest httpRequest &&
                shouldLog(httpRequest)) {
            LOG.debug("Requesting {} {}",
                    httpRequest.getMethod(), httpRequest.getRequestURI());
        }

        chain.doFilter(request, response);
    }

    private boolean shouldLog(HttpServletRequest request) {
        return !isPathExcluded(request.getRequestURI());
    }

    private boolean isPathExcluded(String requestPath) {
        return requestLoggingConfig.getExcludedPaths()
                .stream()
                .anyMatch(requestPath::startsWith);
    }
}