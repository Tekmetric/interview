package com.interview.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bookstoreOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bookstore API")
                        .version("1.0.0")
                        .description("""
                                CRUD API showcasing:
                                - Idempotency via `Idempotency-Key` header (POST / PUT)
                                - Optimistic concurrency control via `version` field
                                - Paginated, filterable list endpoint
                                """)
                        .contact(new Contact()
                                .name("Bookstore Team")
                                .email("balazs@bekesi.com"))
                        .license(new License().name("MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local development")));
    }
}