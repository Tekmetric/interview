package com.interview.config.auth;

import com.interview.config.CorrelationFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for JWT-based authentication and role-based authorization.
 *
 * <p><strong>Security Rules:</strong>
 * <ul>
 *   <li><strong>Public endpoints:</strong> /auth/login, /api/welcome, H2 console, Swagger UI</li>
 *   <li><strong>GET endpoints:</strong> Both ADMIN and USER roles can access</li>
 *   <li><strong>POST/PUT/DELETE:</strong> Only ADMIN role can access</li>
 * </ul>
 *
 * <p><strong>JWT Token:</strong> Required in Authorization header as "Bearer {token}"
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String DEV_PROFILE = "dev";
    private static final String TEST_PROFILE = "test";

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorrelationFilter correlationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF (not needed for JWT)
            .csrf(AbstractHttpConfigurer::disable)

            // Add correlation filter FIRST - before any security processing
            .addFilterBefore(correlationFilter, UsernamePasswordAuthenticationFilter.class)

            // Handle response format for 401 and 403 exceptions
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(
                    (request, response, authException) -> sendErrorResponse(response, 401, "UNAUTHORIZED", "Authentication required",
                        request.getRequestURI()))
                .accessDeniedHandler((request, response, accessDeniedException) -> sendErrorResponse(response, 403, "FORBIDDEN",
                    "Access denied. Administrator privileges required", request.getRequestURI())))

            // Configure authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - no authentication required
                .requestMatchers("/auth/**")
                .permitAll()
                .requestMatchers("/h2-console/**")
                .permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                .permitAll()
                .requestMatchers("/actuator/**")
                .permitAll()

                // Customer API authorization rules
                .requestMatchers(HttpMethod.GET, "/api/v1/customers/**")
                .hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.POST, "/api/v1/customers/**")
                .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/customers/**")
                .hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/customers/**")
                .hasRole("ADMIN")

                // Vehicle API authorization rules
                .requestMatchers(HttpMethod.GET, "/api/v1/vehicles/**")
                .hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.POST, "/api/v1/vehicles/**")
                .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/vehicles/**")
                .hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/vehicles/**")
                .hasRole("ADMIN")

                // ServicePackage API authorization rules
                .requestMatchers(HttpMethod.GET, "/api/v1/service-packages/*/subscribers")
                .hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/service-packages/**")
                .hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.POST, "/api/v1/service-packages/**")
                .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/service-packages/**")
                .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/service-packages/**")
                .hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/service-packages/**")
                .hasRole("ADMIN")

                // All other endpoints require authentication
                .anyRequest()
                .authenticated())

            // Stateless session management (JWT doesn't need sessions)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Add JWT filter AFTER correlation filter
            .addFilterAfter(jwtAuthenticationFilter, CorrelationFilter.class)

            // Configure frame options based on profile
            .headers(headers -> {
                if (DEV_PROFILE.equals(activeProfile) || TEST_PROFILE.equals(activeProfile)) {
                    // DEV/TEST: Remove protection to allow H2 console frames
                    headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable);
                } else {
                    // PROD: Add protection to prevent clickjacking
                    headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny);
                }
            });

        return http.build();
    }

    /**
     * Utility method to send consistent JSON error responses for Spring Security exceptions.
     *
     * <p>Formats error responses in the same structure as ErrorResponse DTO to maintain
     * consistency across all API endpoints.
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String error, String message, String path) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");

        String jsonResponse = String.format("""
            {
                "error": "%s",
                "message": "%s",
                "path": "%s",
                "status": %d,
                "timestamp": "%s"
            }
            """, error, message, path, status, java.time.LocalDateTime.now());

        response.getWriter().write(jsonResponse);
    }
}