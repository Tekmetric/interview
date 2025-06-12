package com.interview.config;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;

@Configuration
public class AuditColumnConfig {

  @Bean(name = "auditingDateTimeProvider")
  public DateTimeProvider dateTimeProvider() {
    return () -> Optional.of(OffsetDateTime.now(ZoneOffset.UTC));
  }

}
