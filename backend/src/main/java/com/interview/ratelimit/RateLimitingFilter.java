package com.interview.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.exception.ApiErrorDto;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class RateLimitingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Value("${app.ratelimit.window-seconds:60}")
    private long windowSeconds;
    @Value("${app.ratelimit.max-requests:20}")
    private int maxRequests;

    // Key -> bucket
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Limit only the CRUD endpoints (not /api/welcome).
        return !path.startsWith("/api/repair-orders");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String key = clientKey(request);
        Bucket bucket = buckets.computeIfAbsent(key, k -> createBucket());

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            long retryAfterSeconds = Math.max(
                    1,
                    (probe.getNanosToWaitForRefill() + 999_999_999L) / 1_000_000_000L
            );

            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));

            // Keep response error contract consistent.
            ApiErrorDto error = new ApiErrorDto(
                    "rate_limited",
                    "Too many requests",
                    429,
                    request.getRequestURI(),
                    Instant.now(),
                    null
            );

            response.getWriter().write(objectMapper.writeValueAsString(error));

            log.warn(
                    "rate_limited key={} path={} retryAfterSeconds={}",
                    key,
                    request.getRequestURI(),
                    retryAfterSeconds
            );
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(maxRequests)
                .refillIntervally(maxRequests, Duration.ofSeconds(windowSeconds))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private static String clientKey(HttpServletRequest request) {
        // Use X-Forwarded-For if present (common behind proxies).
        // Otherwise fall back to remote addr.
        String xff = request.getHeader("X-Forwarded-For");
        return Optional.ofNullable(xff)
                .map(String::trim)
                .filter(v -> !v.isEmpty())
                .map(v -> v.split(",")[0].trim())
                .orElseGet(request::getRemoteAddr);
    }
}
