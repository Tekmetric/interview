package com.interview.config.auth;

import com.interview.service.auth.JwtService;
import com.interview.service.auth.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT authentication filter for processing JWT tokens in requests.
 *
 * <p>Extracts JWT token from Authorization header, validates it, and sets
 * Spring Security authentication context for role-based access control.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
        throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Skip if no Authorization header or doesn't start with Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract token from header
            jwt = authHeader.substring(7);
            username = jwtService.extractUsername(jwt);

            // If username exists and no authentication in context
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null && userService.findByUsername(username).isPresent()
                && jwtService.validateToken(jwt, username)) {

                // Extract role and create authorities
                var role = jwtService.extractRole(jwt);
                var authorities = java.util.List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));

                // Create authentication token
                var authToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.debug("JWT authentication successful for user: {} with role: {}", username, role);
            }

        } catch (Exception ex) {
            log.warn("JWT authentication failed: {}", ex.getMessage());
            // Don't set authentication - will result in 401 for protected endpoints
        }

        filterChain.doFilter(request, response);
    }
}