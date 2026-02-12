package com.interview.persistence;

import com.interview.util.SecurityUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.ZonedDateTime;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider", dateTimeProviderRef = "auditingDateTimeProvider")
@EnableJpaRepositories(basePackages = "com.interview")
public class JPAConfiguration {

    @Bean(name = "auditorProvider")
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of(SecurityUtil.getAuthenticatedUser());
    }

    @Bean(name = "auditingDateTimeProvider")
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(ZonedDateTime.now());
    }
}
