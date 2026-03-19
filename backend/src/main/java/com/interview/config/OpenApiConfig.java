package com.interview.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI employeeApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Employee CRUD API")
                        .description("REST API for employee management with create, read, update, delete. " +
                                "Supports pagination, idempotency keys for create, and optimistic locking.")
                        .version("v1"))
                .servers(List.of(
                        new Server().url("/").description("Current host")));
    }
}
