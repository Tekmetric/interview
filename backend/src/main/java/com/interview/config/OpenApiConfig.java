package com.interview.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Repair Order API",
        version = "1.0",
        description = "REST API for managing repair orders, customers, and line items",
        contact = @Contact(name = "Tekmetric Interview")
    )
)
public class OpenApiConfig {
}
