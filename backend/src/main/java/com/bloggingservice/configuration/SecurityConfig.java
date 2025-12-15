package com.bloggingservice.configuration;

import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  /** Setup a security chain that uses basic authentication for all API based calls */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            authorize ->
                authorize.requestMatchers("/api/**").authenticated().anyRequest().permitAll())
        .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
        .httpBasic(Customizer.withDefaults());

    return http.build();
  }

  /**
   * This is only set for local development. A deployed version will have a correct, non-in-memory
   * user service
   */
  @Profile("!prod")
  @Bean
  public UserDetailsService userDetailsService() {
    Map<String, String> users =
        Map.of(
            "Bob", "foo",
            "Alice", "bar");
    List<UserDetails> userDetails =
        users.entrySet().stream()
            .map(
                entry ->
                    User.withDefaultPasswordEncoder()
                        .username(entry.getKey())
                        .password(entry.getValue())
                        .roles("USER")
                        .build())
            .toList();

    return new InMemoryUserDetailsManager(userDetails);
  }
}
