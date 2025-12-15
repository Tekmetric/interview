package com.bloggingservice.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Map;

@Configuration
public class OpenApiConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/swagger-ui.html");
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Blogger Service")
                        .version("1.0.0")
                        .summary("A sample service for serving blog content"))
                .servers(List.of(new Server().url("http://localhost:8080").description("Local instance")))
                .security(List.of(new SecurityRequirement().addList("BasicAuth")))
                .components(new Components()
                        .securitySchemes(Map.of("BasicAuth", new SecurityScheme()
                                .description("Basic Authorization")
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("Basic")
                                .in(SecurityScheme.In.HEADER))));
    }
}