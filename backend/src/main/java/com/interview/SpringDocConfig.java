package com.interview;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Customer API", version = "v1", description = "This API performs create/read/update/delete operations" +
        " for customer entities."))
public class SpringDocConfig {
}