package com.interview.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@ConditionalOnProperty(
    name = {"spring.security.enabled"},
    havingValue = "true",
    matchIfMissing = true
)
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Value("${spring.profiles.active:}")
  private String activeProfiles;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authz -> authz
            // Allow public access to actuator endpoints
            .requestMatchers("/actuator/**").permitAll()

            // Allow public access to OpenAPI/Swagger URLs (Swagger UI is only enabled in dev profile)
            .requestMatchers("/v3/api-docs/**").permitAll()
            .requestMatchers("/swagger-ui/**").permitAll()
            .requestMatchers("/swagger-ui.html").permitAll()
            .requestMatchers("/swagger-resources/**").permitAll()

            // Require authentication for all other endpoints
            .anyRequest().authenticated()
        )
        .httpBasic(Customizer.withDefaults())

        // Disable CSRF for REST APIs
        .csrf(AbstractHttpConfigurer::disable);

    // Disable frame options in dev profile to allow H2 console to work properly as it uses frames
    if (activeProfiles.contains("dev")) {
      http.headers(headers -> headers
          .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
      );
    } else {
      http.headers(headers -> headers
          .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
      );
    }

    return http.build();
  }

}
