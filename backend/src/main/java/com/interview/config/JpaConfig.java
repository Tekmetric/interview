package com.interview.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables JPA auditing (@CreatedDate / @LastModifiedDate). Kept separate from Application
 * so that Spring's @WebMvcTest slice doesn't pick it up and try to wire JPA beans.
 *
 * <p>@DataJpaTest does not autoload arbitrary @Configuration classes — repository tests
 * that rely on auditing must @Import this config explicitly.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
