package com.interview.config;

import com.interview.entity.Role;
import com.interview.filter.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

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
                    // Allow all to access H2 console URLs
                    .requestMatchers("/h2-console/**").permitAll()
                    // Allow all to access login
                    .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                    // Allow all to refresh access token
                    .requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll()
                    // Allow all to create a customer (no authentication required)
                    .requestMatchers(HttpMethod.POST, "/api/customers").permitAll()
                    // Allow all to access OpenAPI/Swagger documentation
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**").permitAll()
                    // Only ADMIN can DELETE a customer by ID
                    .requestMatchers(HttpMethod.DELETE, "/api/customers/{id}").hasRole(Role.ADMIN.name())
                    // Require authentication for all others
                    .anyRequest().authenticated()
            )

            // Add custom JWT filter (jwtAuthenticationFilter) into the filter chain and specifically to
            // place it just before the built-in UsernamePasswordAuthenticationFilter, so that token-based auth is
            // processed before any default username/password authentication logic.
            // If the JWT is valid, jwtAuthenticationFilter sets the authentication, and the request can proceed
            // without triggering form login.
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

            .exceptionHandling(c -> {
                // When an unauthenticated request tries to access a protected resource, return 401
                // E.g., if no/invalid/expired JWT Access token, return 401
                c.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
                // When the user is logged in but lacks the required role/authority, return 403
                // E.g., if wrong role, return 403
                c.accessDeniedHandler(
                    (request, response, accessDeniedException) ->
                    response.setStatus(HttpStatus.FORBIDDEN.value())
                );
            });

        return http.build();
    }
}