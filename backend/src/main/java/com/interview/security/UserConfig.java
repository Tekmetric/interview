package com.interview.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserConfig {

  @Bean
  public UserDetailsService userDetailsService() {
    final UserDetails sorin =
        User.withUsername("sorin")
            .password(passwordEncoder().encode("pass123"))
            .authorities("READ", "WRITE")
            .build();

    final UserDetails guest =
        User.withUsername("guest")
            .password(passwordEncoder().encode("guest"))
            .authorities("READ")
            .build();

    return new InMemoryUserDetailsManager(sorin, guest);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
