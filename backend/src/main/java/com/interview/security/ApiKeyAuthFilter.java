package com.interview.security;

import com.interview.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final ApiKeyRepository apiKeyRepository;
    private final String apiKeyHeaderName;

    public ApiKeyAuthFilter(
            ApiKeyRepository apiKeyRepository,
            @Value("${app.api-key-header:X-API-Key}") String apiKeyHeaderName) {
        this.apiKeyRepository = apiKeyRepository;
        this.apiKeyHeaderName = apiKeyHeaderName;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Public endpoints
        if (path == null || path.startsWith("/api/welcome")) {
            return true;
        }

        // API-only auth: everything else is not handled here
        if (!path.startsWith("/api/")) {
            return true;
        }

        // Allow preflight requests to avoid blocking tooling
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String providedKey = request.getHeader(apiKeyHeaderName);

        Optional<com.interview.model.ApiKey> apiKey = Optional.empty();
        if (providedKey != null && !providedKey.isBlank()) {
            apiKey = apiKeyRepository.findByApiKeyAndActiveTrue(providedKey);
        }

        if (apiKey.isEmpty()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        filterChain.doFilter(request, response);
    }
}

