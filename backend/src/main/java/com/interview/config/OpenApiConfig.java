package com.interview.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI jobPostingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Job Postings API")
                        .description("""
                                ## Job Management System
                                CRUD API for managing job postings / vacancies.
                                
                                **Key Features:**
                                * Full-text title search
                                * Remote/location filtering
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Tekmetric Support")
                                .email("support@tekmetric.com")
                                .url("https://tekmetric.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local development server")));
    }
}
