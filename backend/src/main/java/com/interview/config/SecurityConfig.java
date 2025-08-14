package com.interview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Use stateless sessions (token-based auth)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Disable CSRF. CSRF is not needed because REST APIs uses explicit token-based authentication
                .csrf(AbstractHttpConfigurer::disable)

                // Disable default login form
                .formLogin(AbstractHttpConfigurer::disable)

                // Allow H2 console frame loading
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                )

                // Authorize requests
                .authorizeHttpRequests(auth -> auth
                        // Allow all to access /api/customers/**
                        .requestMatchers("/api/customers/**").permitAll()
                        // Allow all to access H2 console URLs
                        .requestMatchers("/h2-console/**").permitAll()
                        // Allow all others (adjust if needed)
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}