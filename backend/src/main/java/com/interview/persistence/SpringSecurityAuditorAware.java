package com.interview.persistence;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("anonymous");
    }
}