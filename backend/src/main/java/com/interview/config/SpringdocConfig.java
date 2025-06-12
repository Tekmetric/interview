package com.interview.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Springdoc configuration for OpenAPI documentation.
 * This configuration sets up basic authentication in Swagger UI for the API endpoints.
 */
@Configuration
@SecurityScheme(
    type = SecuritySchemeType.HTTP,
    name = "basicAuth",
    scheme = "basic")
public class SpringdocConfig {

}
