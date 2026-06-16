package com.interview.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String BASIC_AUTH_SCHEME = "basicAuth";

    @Bean
    public OpenAPI dealershipOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Customer Financing & Credit Application API")
                        .description("""
                                Car dealership credit application service.

                                Supports full CRUD for Customers and Credit Applications,
                                with state-machine-driven status transitions
                                (SUBMITTED → UNDER_REVIEW → APPROVED | DENIED).

                                AWS integrations (SQS, S3) are stubbed by default —
                                set aws.enabled=true to activate real AWS connectivity.
                                """)
                        .version("v1")
                        .contact(new Contact()
                                .name("Interview Project")
                                .email("dev@tekmetric.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local development")))
                .addSecurityItem(new SecurityRequirement().addList(BASIC_AUTH_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(BASIC_AUTH_SCHEME, new SecurityScheme()
                                .name(BASIC_AUTH_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")));
    }
}
