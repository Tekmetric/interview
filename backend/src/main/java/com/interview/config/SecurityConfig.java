package com.interview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * This is a simple security configuration for demonstration purposes.
 *
 * It sets up in-memory authentication with two users: 'service' and 'readonly'.
 * (1) The 'service' user has the role SERVICE and can perform write operations
 * (2) 'readonly' user has the role READONLY and can only perform read operations.
 *
 * In production, we would use a more secure and robust authentication mechanism like OAuth2 with JWT tokens
 * to do stateless authentication for API clients and probably integrate with an identity provider for SSO.
 *
 * We disable CSRF protection since this is a REST API demo. We would need to re-enable CSRF if we were also creating a UI client.
 * I have implemented the double submit cookie pattern before and would do so again in a real-world scenario if applicable.
 *
 * Finally, we permit access to the H2 console, Swagger UI for testing, nd the default welcome endpoint without authentication.
 * All other endpoints require authentication.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        var serviceUser = User.withUsername("service").password("service123").roles("SERVICE").build();
        var readonlyUser = User.withUsername("readonly").password("readonly123").roles("READONLY").build();
        return new InMemoryUserDetailsManager(serviceUser, readonlyUser);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/api/welcome").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
        return http.build();
    }
}
