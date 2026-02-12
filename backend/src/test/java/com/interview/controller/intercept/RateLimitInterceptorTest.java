package com.interview.controller.intercept;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class RateLimitInterceptorTest {

  private static final String REMAINING_HEADER = "X-RateLimit-Remaining";
  private static final String RETRY_HEADER = "X-RateLimit-Retry-After-Seconds";

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private Object handler;
  @Mock private Bucket bucket;
  @Mock private ConsumptionProbe probe;

  private RateLimitInterceptor interceptor;

  @BeforeEach
  void setUp() {
    interceptor = new RateLimitInterceptor(bucket);
    when(bucket.tryConsumeAndReturnRemaining(anyLong())).thenReturn(probe);
  }

  @Test
  void preHandle_acceptsRequest_whenLimitNotExceeded() throws Exception {
    final long remaining = 10;

    when(probe.isConsumed()).thenReturn(true);
    when(probe.getRemainingTokens()).thenReturn(remaining);

    final boolean result = interceptor.preHandle(request, response, handler);

    assertTrue(result);
    verify(response).addHeader(REMAINING_HEADER, String.valueOf(remaining));
  }

  @Test
  void preHandle_rejectsRequest_whenLimitExceeded() throws Exception {
    final long remainingNanos = 1_000_000_000;

    when(probe.isConsumed()).thenReturn(false);
    when(probe.getNanosToWaitForRefill()).thenReturn(remainingNanos);

    final boolean result = interceptor.preHandle(request, response, handler);

    assertFalse(result);
    verify(response)
        .addHeader(
            RETRY_HEADER,
            String.valueOf(
                TimeUnit.NANOSECONDS.convert(probe.getNanosToWaitForRefill(), TimeUnit.SECONDS)));
    verify(response).sendError(eq(HttpStatus.TOO_MANY_REQUESTS.value()), anyString());
  }
}
