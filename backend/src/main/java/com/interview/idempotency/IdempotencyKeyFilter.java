package com.interview.idempotency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(1)
public class IdempotencyKeyFilter extends org.springframework.web.filter.OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyKeyFilter.class);
    private static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";
    private static final String PATH_CREATE_EMPLOYEE = "/api/v1/employees";
    private static final String METHOD_POST = "POST";

    private final IdempotencyStore idempotencyStore;

    public IdempotencyKeyFilter(IdempotencyStore idempotencyStore) {
        this.idempotencyStore = idempotencyStore;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        if (!METHOD_POST.equalsIgnoreCase(method) || path == null) {
            return true;
        }
        String normalized = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        return !PATH_CREATE_EMPLOYEE.equals(normalized);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String key = request.getHeader(IDEMPOTENCY_KEY_HEADER);
        if (key == null || key.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }
        key = key.trim();

        IdempotencyRecord existing = idempotencyStore.get(key);
        if (existing != null) {
            log.debug("Idempotency cache hit: key={}", key);
            byte[] body = existing.body();
            response.setStatus(existing.status());
            response.setContentType(existing.contentType());
            if (body.length > 0) {
                response.setContentLength(body.length);
                response.getOutputStream().write(body);
            }
            return;
        }

        ContentCachingResponseWrapper wrappingResponse = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(request, wrappingResponse);

        int status = wrappingResponse.getStatus();
        byte[] responseBody = wrappingResponse.getContentAsByteArray();
        String contentType = wrappingResponse.getContentType();
        if (contentType == null) {
            contentType = "application/json";
        }
        if (status >= 200 && status < 300) {
            idempotencyStore.put(key, new IdempotencyRecord(status, responseBody, contentType, System.currentTimeMillis()));
        }
        wrappingResponse.copyBodyToResponse();
    }
}
