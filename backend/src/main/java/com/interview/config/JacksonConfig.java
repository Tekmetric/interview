package com.interview.config;

import com.fasterxml.jackson.databind.Module;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    //Register module to support JsonNullable in request models
    @Bean
    public Module jsonNullableModule() {
        return new JsonNullableModule();
    }
}
