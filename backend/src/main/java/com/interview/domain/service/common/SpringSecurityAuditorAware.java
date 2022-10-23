package com.interview.domain.service.common;

import com.interview.application.aspect.log.annotations.ExcludeFromLoggingAspect;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation of AuditorAware based on Spring Security.
 */
@Component
@ExcludeFromLoggingAspect
public class SpringSecurityAuditorAware implements AuditorAware<String> {
    public static final String SYSTEM_ACCOUNT = "system";

    @Override
    public Optional<String> getCurrentAuditor() {
        // this is a dummy default system value. If spring security is used the logged-in user should be used.
        return Optional.of(SYSTEM_ACCOUNT);
    }
}
