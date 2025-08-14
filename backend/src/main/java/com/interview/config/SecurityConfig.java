package com.interview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
        // Use stateless sessions (token-based authentication)
        http.sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Disable CSRF. CSRF is not needed because REST APIs uses explicit token-based authentication
                .csrf(AbstractHttpConfigurer::disable)
                // Authorize
                .authorizeHttpRequests(c -> c
                        // allow all /api/customers apis
                        .requestMatchers("/api/customers/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}