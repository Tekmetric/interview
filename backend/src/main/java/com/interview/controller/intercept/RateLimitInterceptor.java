package com.interview.controller.intercept;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

  private static final String REMAINING_HEADER = "X-RateLimit-Remaining";
  private static final String RETRY_HEADER = "X-RateLimit-Retry-After-Seconds";

  private final Bucket bucket;

  @Override
  public boolean preHandle(
      final HttpServletRequest request, final HttpServletResponse response, final Object handler)
      throws Exception {

    final ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
    if (probe.isConsumed()) {
      response.addHeader(REMAINING_HEADER, String.valueOf(probe.getRemainingTokens()));
      return true;
    } else {
      log.warn("Entering rate limited state");
      long waitForRefill =
          TimeUnit.NANOSECONDS.convert(probe.getNanosToWaitForRefill(), TimeUnit.SECONDS);
      response.addHeader(RETRY_HEADER, String.valueOf(waitForRefill));
      response.sendError(
          HttpStatus.TOO_MANY_REQUESTS.value(), "You have exhausted your API Request Quota");
      return false;
    }
  }
}
