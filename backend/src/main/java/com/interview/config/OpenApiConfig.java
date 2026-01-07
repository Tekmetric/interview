package com.interview.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Repair Shop API")
                        .version("1.0")
                        .description("REST API for managing customers and vehicles in a repair shop")
                        .contact(new Contact()
                                .name("Repair Shop Team")
                                .email("support@repairshop.com")));
    }
}
