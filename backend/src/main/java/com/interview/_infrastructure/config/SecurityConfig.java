package com.interview._infrastructure.config;

import com.interview._infrastructure.security.ApiKeyAuthFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.interview._infrastructure.security.ApiKeyAuthFilter.AUTHENTICATED_PATH;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String[] PERMITTED_PATHS = {
            "/actuator/health",
            "/h2-console/**",
            "/api/welcome",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };
    private static final String AUTHENTICATED_PATH_MATCHER = AUTHENTICATED_PATH + "/**";

    private final ApiKeyAuthFilter apiKeyAuthFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
                .antMatchers(PERMITTED_PATHS).permitAll()
                .antMatchers(AUTHENTICATED_PATH_MATCHER).authenticated()
                .anyRequest().denyAll()
            .and()
            .headers().frameOptions().sameOrigin();
    }
}
