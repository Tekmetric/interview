package com.interview.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * Test configuration for JPA auditing in repository tests.
 *
 * <p>@DataJpaTest doesn't include all application configurations by default,
 * so we need to explicitly enable JPA auditing for repository tests that
 * test entities with audit fields.
 */
@TestConfiguration
@EnableJpaAuditing
public class TestJpaConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("TEST_USER");
    }
}