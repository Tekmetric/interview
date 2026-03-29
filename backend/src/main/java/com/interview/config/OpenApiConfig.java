package com.interview.config;

import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.ErrorResponse;

import java.util.Map;

@Configuration
public class OpenApiConfig {

    /**
     * Customizes the generated OpenAPI documentation.
     * This class replaces the verbose default schema for Spring's ErrorResponse
     * with a clean, simplified version.
     */
    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            Schema<?> errorSchema = new ObjectSchema()
                    .addProperty("title", new Schema<String>().type("string").description("A short, human-readable summary of the problem type."))
                    .addProperty("status", new Schema<Integer>().type("integer").description("The HTTP status code."))
                    .addProperty("detail", new Schema<String>().type("string").description("A human-readable explanation specific to this occurrence of the problem."))
                    .addProperty("errors", new Schema<Map<String, String>>().type("object").description("A map of field names to validation error messages."));

            openApi.getComponents().getSchemas().put(ErrorResponse.class.getSimpleName(), errorSchema);
        };
    }
}
