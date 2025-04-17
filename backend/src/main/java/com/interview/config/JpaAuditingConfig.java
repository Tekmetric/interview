package com.interview.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/*
 Enable JPA Auditing which allows us to have @CreatedDate and @LastModifiedDate fields on entities automatically.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
