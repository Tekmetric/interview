package com.tekmetric.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class MdcRequestLoggingFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(MdcRequestLoggingFilter.class);

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    long start = System.currentTimeMillis();

    String requestId =
        Optional.ofNullable(request.getHeader("X-Request-Id")).orElse(UUID.randomUUID().toString());

    MDC.put("requestId", requestId);
    MDC.put("path", request.getRequestURI());
    MDC.put("method", request.getMethod());

    try {
      filterChain.doFilter(request, response);
    } finally {
      long duration = System.currentTimeMillis() - start;
      MDC.put("status", String.valueOf(response.getStatus()));
      MDC.put("durationMs", String.valueOf(duration));

      // One structured line per request – easy to filter/aggregate
      log.info("request completed");

      MDC.clear();
    }
  }
}
