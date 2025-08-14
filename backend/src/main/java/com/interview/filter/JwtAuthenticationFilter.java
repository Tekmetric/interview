package com.interview.filter;

import com.interview.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null) {
            System.out.println("No JWT token found");
            // Resume processing across the chain because some of the APIs does not require
            filterChain.doFilter(request, response);
            return;
        }

        // authHeader.startsWith("Bearer ") enforces the Authorization header to start with Bearer, to follow rest convention
        if (!authHeader.startsWith("Bearer ")) {
            System.out.println("JWT does not start with Bearer");
            // Resume processing across the chain because some of the APIs does not require
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.replace("Bearer ", "");
        if (!jwtService.validateToken(token)) {
            System.out.println("Invalid JWT token");
            // Resume processing across the chain because some of the APIs does not require
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("JWT token is valid");
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            jwtService.getEmailFromToken(token),
            null,
            null
        );
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}