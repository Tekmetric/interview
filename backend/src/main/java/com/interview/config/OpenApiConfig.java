package com.interview.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public GroupedOpenApi v1() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/api/v1/**")
                .addOpenApiCustomizer(openApi -> openApi.setInfo(new Info()
                        .title("Auto Service API")
                        .version("v1")
                        .description("Repair Order API")))
                .build();
    }
}
