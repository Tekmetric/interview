package com.interview.filter;

import com.interview.dto.Jwt;
import com.interview.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null) {
            log.warn("No JWT token found");
            // Resume processing across the chain because some of the APIs does not require
            filterChain.doFilter(request, response);
            return;
        }

        // authHeader.startsWith("Bearer ") enforces the Authorization header to start with Bearer, to follow rest convention
        if (!authHeader.startsWith("Bearer ")) {
            log.warn("JWT does not start with Bearer");
            // Resume processing across the chain because some of the APIs does not require
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.replace("Bearer ", "");
        Jwt jwt = jwtService.parseToken(token);
        if (jwt == null || jwt.isExpired()) {
            log.warn("Invalid JWT token");
            // Resume processing across the chain because some of the APIs does not require
            filterChain.doFilter(request, response);
            return;
        }

        log.info("JWT token is valid");
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            // Use the customer id (UUID) as principal of the authentication
            jwt.getCustomerId(),
            null,
            List.of(new SimpleGrantedAuthority("ROLE_" + jwt.getRole()))
        );
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}