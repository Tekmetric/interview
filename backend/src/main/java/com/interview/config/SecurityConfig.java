package com.interview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
//@EnableMethodSecurity
//@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain with OAuth2 token validation.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // What paths do we want to authenticate
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll() // Allow all actuator endpoints
                        .requestMatchers(HttpMethod.GET, "/api/welcome").permitAll() // Allow initial resource to be public
                        .anyRequest().authenticated() // All other requests require authentication (a valid JWT)
                )
                // Configure OAuth2 Resource Server support
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()) // Specify the converter for authorities
                        )
                );

        return http.build();
    }

    /**
     * Configures how JWT claims are mapped to GranterAuthorities.
     * Needed for role-based authorization (@PreAuthorize).
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        // Default converter extracts scopes/claims and prefixes them with "SCOPE_"
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // We use the "roles" JWT claim to store the roles/permissions
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

        // By default, hasRole('ADMIN') expects "ROLE_ADMIN" and our tokens do not have that prefix.
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_"); // Set prefix to match hasRole() expectations

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
