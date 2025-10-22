package com.interview.web.ratelimit;

import com.interview.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import io.github.bucket4j.Bucket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Component
public class RateLimiterInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RateLimiterInterceptor.class);

    private final RateLimitService rateLimitService;
    private final Map<String, Bucket> buckets = new HashMap<>();

    public RateLimiterInterceptor(final RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    public boolean preHandle(final HttpServletRequest httpRequest,
                             final HttpServletResponse httpResponse,
                             final Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            return extracted(httpRequest, httpResponse, (HandlerMethod) handler);
        }
        logger.info("Unknown handler method: {} for request: {}",
                    handler.getClass().getName(),
                    httpRequest.getRequestURI());
        return true;
    }

    private boolean extracted(final HttpServletRequest httpRequest,
                              final HttpServletResponse httpResponse,
                              final HandlerMethod handlerMethod) throws IOException {
        final var key = resolveKey(httpRequest, handlerMethod);
        final var bucket = this.resolveBucket(key, handlerMethod);
        final var probe = bucket.tryConsumeAndReturnRemaining(1);

        logger.trace("Bucket: {} has {} remaining tokens", key, probe.getRemainingTokens());
        if (probe.isConsumed()) {
            httpResponse.setHeader("X-Rate-Limit-Remaining", Long.toString(probe.getRemainingTokens()));
            return true;
        } else {
            final var waitForRefill = TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill());

            httpResponse.setContentType("text/plain");
            httpResponse.setHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.getWriter().append("Too many requests");

            logger.warn("Rate limit reached for request {}", httpRequest.getRequestURI());
            return false;
        }
    }

    private String resolveKey(final HttpServletRequest httpRequest, final HandlerMethod handlerMethod) {
        final var prefix = "ratelimit";
        final var client = SecurityUtil.isAuthenticated()
            ? SecurityUtil.getAuthenticatedUser()
            : httpRequest.getRemoteAddr();
        final var bucketConfigurationKey = createBucketConfigurationKey(handlerMethod);
        final var key = this.rateLimitService.existsBucketConfiguration(bucketConfigurationKey)
            ? bucketConfigurationKey
            : "default";

        return "%s:%s:%s".formatted(prefix, client, key);
    }

    private String createBucketConfigurationKey(final HandlerMethod handlerMethod) {
        final var bucketConfigurationKey = "%s#%s".formatted(handlerMethod.getBeanType().getName(),
                                                             handlerMethod.getMethod().getName());
        return this.rateLimitService.existsBucketConfiguration(bucketConfigurationKey)
            ? bucketConfigurationKey
            : "default";
    }

    private Bucket resolveBucket(final String key, final HandlerMethod handlerMethod) {
        final var bucketConfigurationKey = createBucketConfigurationKey(handlerMethod);
        final var bucketConfiguration = this.rateLimitService.resolveBucketConfiguration(bucketConfigurationKey);
        return this.buckets.computeIfAbsent(key, k -> {
            final var builder = Bucket.builder();
            Stream.of(bucketConfiguration.getBandwidths()).forEach(builder::addLimit);
            return builder.build();
        });
    }

}