package com.interview.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// Configuration class for OpenAPI/Swagger documentation.
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            // API metadata and information
            .info(new Info()
                .title("Customer Management API")
                .version("1.0")
                .description("API for managing customers"))
            
            // Global security requirement - all endpoints require bearer token. Swagger UI will show the "Authorize" button.
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            
            // Security scheme definitions
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}