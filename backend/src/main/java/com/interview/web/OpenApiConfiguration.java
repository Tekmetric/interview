package com.interview.web;

import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public GroupedOpenApi projectsOpenApi() {
        return GroupedOpenApi.builder()
            .group("Projects")
            .pathsToMatch("/api/projects/**")
            .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        final var info = new Info()
            .title("Tekmetric REST API")
            .version("1.0.0")
            .description("This API exposes endpoints to access Tekmetric API.");

        return new OpenAPI()
            .info(info)
            .servers(List.of(new Server()
                .url("http://localhost:8080/api")
                .description("Server URL in for this environment")));
    }
}
