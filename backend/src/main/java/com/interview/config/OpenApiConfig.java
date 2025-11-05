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
    public OpenAPI customOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Development Server");

        Contact contact = new Contact();
        contact.setName("Commercial Kitchen Management Team");
        contact.setEmail("support@kitchenmanagement.com");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Commercial Kitchen Ingredient Management API")
                .version("1.0.0")
                .description("REST API for managing ingredients in a commercial kitchen environment. " +
                        "This API provides comprehensive CRUD operations for ingredient inventory management, " +
                        "including tracking quantities, suppliers, expiration dates, and refrigeration requirements.")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(Collections.singletonList(localServer));
    }
}
