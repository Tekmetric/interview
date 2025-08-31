package com.interview.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @NonNull
    @Override
    public Optional<String> getCurrentAuditor() {
        // In a real application, fetch the current user from the security context
        // eg. SecurityContextHolder.getContext().getAuthentication().getName();
        return Optional.of("system");
    }
}
