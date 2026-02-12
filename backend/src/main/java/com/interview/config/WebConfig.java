package com.interview.config;

import com.interview.controller.intercept.RateLimitInterceptor;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("!test")
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    final Bucket bucket =
        Bucket.builder()
            .addLimit(
                Bandwidth.builder().capacity(5).refillGreedy(5, Duration.ofSeconds(10)).build())
            .build();
    registry.addInterceptor(new RateLimitInterceptor(bucket));
  }
}
