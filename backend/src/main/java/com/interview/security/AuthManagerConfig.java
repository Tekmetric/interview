package com.interview.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthManagerConfig {
  @Bean
  public AuthenticationManager authenticationManager(
      final UserDetailsService uds, final PasswordEncoder encoder) {
    final DaoAuthenticationProvider provider = new DaoAuthenticationProvider(uds);
    provider.setPasswordEncoder(encoder);
    return new ProviderManager(provider);
  }
}
