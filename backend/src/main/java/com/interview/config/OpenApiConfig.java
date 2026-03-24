package com.interview.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Vehicle API",
                version = "1.0",
                description = "REST API for managing vehicles with filtering, pagination, validation, and optimistic locking."
        )
)
public class OpenApiConfig {}