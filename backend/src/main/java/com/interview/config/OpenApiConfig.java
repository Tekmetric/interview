package com.interview.config;

import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
public class OpenApiConfig {
   @Bean
    public OpenAPI customOpenAPI() {
       return new OpenAPI()
           .info(new Info()
               .title("Repair Job API")
               .version("1.0")
               .summary("This API lets you do CRUD operations on Repair Jobs")
               .termsOfService("This API can only be used internally"))
           .servers(List.of(new Server().url("http://localhost:8080").description("Local Instance")));
   }
}
