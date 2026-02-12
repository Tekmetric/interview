package com.interview.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static boolean isAuthenticated() {
        final var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null
            && auth.isAuthenticated()
            && auth instanceof UsernamePasswordAuthenticationToken;
    }

    public static String getAuthenticatedUser() {
        final var auth = SecurityContextHolder.getContext().getAuthentication();
        return switch (auth) {
            case UsernamePasswordAuthenticationToken token -> token.getName();
            default -> "anonymous";
        };
    }
}
