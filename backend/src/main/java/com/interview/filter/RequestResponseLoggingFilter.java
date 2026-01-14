package com.interview.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
@Slf4j
@Order(1)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        long startTime = System.currentTimeMillis();
        
        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            
            String headers = Collections.list(request.getHeaderNames())
                    .stream()
                    .map(headerName -> headerName + "=" + maskHeaderValue(headerName, request.getHeader(headerName)))
                    .collect(Collectors.joining(", "));
            
            String queryString = request.getQueryString() != null ? request.getQueryString() : "";
            
            log.info("Request - Method: {}, URI: {}, Query: {}, Status: {}, Duration: {}ms, Headers: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    queryString,
                    response.getStatus(),
                    duration,
                    headers
            );
        }
    }

    private String maskHeaderValue(String headerName, String value) {
        if ("authorization".equalsIgnoreCase(headerName) && value != null) {
            return value.replaceAll("Bearer\\s+[A-Za-z0-9-_]+", "Bearer ***");
        }
        return value;
    }
}
