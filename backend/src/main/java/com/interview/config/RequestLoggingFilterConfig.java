package com.interview.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * This configuration class sets up a request logging filter that logs incoming HTTP requests. It is
 * enabled based on the property 'log.requests.enabled'. The filter can log query strings, payloads,
 * client info, and headers based on the configuration.
 */
@ConditionalOnProperty(
    name = {"log.requests.enabled"},
    havingValue = "true"
)
@Configuration
public class RequestLoggingFilterConfig {

  private static final String PREFIX = "Request payload: ";

  @Value("${log.requests.include-headers:true}")
  private boolean includeHeaders;

  @Bean
  public CommonsRequestLoggingFilter logFilter() {
    CommonsRequestLoggingFilter filter
        = new CommonsRequestLoggingFilter();
    filter.setIncludeQueryString(true);
    filter.setIncludePayload(true);
    filter.setIncludeClientInfo(true);
    filter.setMaxPayloadLength(10000);
    filter.setIncludeHeaders(includeHeaders);
    filter.setAfterMessagePrefix(PREFIX);
    return filter;
  }

}
