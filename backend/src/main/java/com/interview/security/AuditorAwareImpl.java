package com.interview.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authObject = SecurityContextHolder.getContext().getAuthentication();
        String username = (authObject != null && authObject.getPrincipal() instanceof UserPrincipal)
                ? ((UserPrincipal) authObject.getPrincipal()).getUsername()
                : "system";

        return Optional.of(username);
    }
}