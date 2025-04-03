package com.interview.runningevents.infrastructure.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Configuration class for customizing the OpenAPI documentation.
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI runningEventsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Running Events API")
                        .description("RESTful API for managing running events")
                        .version("1.0.0")
                        .contact(new Contact().name("Development Team").email("dev@example.com"))
                        .license(new License().name("MIT License").url("https://opensource.org/licenses/MIT")))
                .externalDocs(new ExternalDocumentation()
                        .description("Running Events API Documentation")
                        .url("https://github.com/myorg/running-events-api"))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development server"),
                        new Server().url("https://api.example.com").description("Production server")));
    }
}
