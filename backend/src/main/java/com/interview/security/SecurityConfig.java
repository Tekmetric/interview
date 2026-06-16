package com.interview.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnWebApplication
public class SecurityConfig implements WebMvcConfigurer {

    @Bean
    public SecurityFilterChain getSecurityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/person/**").hasRole("ADMIN")
                .requestMatchers("/api/welcome").permitAll()
                .anyRequest().authenticated()
        ).httpBasic(Customizer.withDefaults());

        return http.build();

    }

    @Bean
    public UserDetailsService userDetailsService() {

        UserDetails adminUser = User.withUsername("admin").password("$2a$10$szBc7sLDzppyF1UVkWJ0hO0gz7Wavfz8FnVo5FxKyRYBdfCGRnKn2").roles("ADMIN").build();

        return new InMemoryUserDetailsManager(adminUser);

    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();

    }

}
