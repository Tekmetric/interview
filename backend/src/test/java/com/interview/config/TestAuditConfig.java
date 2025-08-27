package com.interview.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@TestConfiguration
@EnableJpaAuditing(auditorAwareRef = "testAuditorProvider")
public class TestAuditConfig {

    @Bean
    @Primary
    public AuditorAware<String> testAuditorProvider() {
        return new TestAuditorAware();
    }

    public static class TestAuditorAware implements AuditorAware<String> {
        // Default user to be used in data jpa tests
        @Override
        public Optional<String> getCurrentAuditor() {
            return Optional.of("test-user");
        }
    }
}
