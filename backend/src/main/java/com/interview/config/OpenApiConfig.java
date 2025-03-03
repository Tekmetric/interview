package com.interview.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server()
            .url("http://localhost:8080")
            .description("Development server");

        Contact contact = new Contact()
            .name("Animal Shelter API Support")
            .email("support@animalshelter.com");

        License mitLicense = new License()
            .name("MIT License")
            .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
            .title("Animal Shelter Management API")
            .version("1.0")
            .contact(contact)
            .description("This API exposes endpoints to manage an animal shelter.")
            .license(mitLicense);

        return new OpenAPI()
            .info(info)
            .servers(Collections.singletonList(devServer));
    }
}