package com.interview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

/**
 * Main Spring Boot application class.
 *
 * <p>Entry point for the customer management application.
 */
@SpringBootApplication(exclude = {
    UserDetailsServiceAutoConfiguration.class // Disable default user - we use JWT authentication
})
// Enable pagination DTOs instead of raw Page objects
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
