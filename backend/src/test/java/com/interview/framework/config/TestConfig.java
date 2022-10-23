package com.interview.framework.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.interview.Application;
import com.interview.application.configuration.jakson.View;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@TestConfiguration
@Import(Application.class)
public class TestConfig {
    @Bean
    @Primary
    public ObjectMapper objectMapperBuilder() {
        ObjectMapper objectMapper = new ObjectMapper();
        SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
        serializationConfig = serializationConfig.with(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
        serializationConfig = serializationConfig.withView(View.Internal.class);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setConfig(serializationConfig);
        return objectMapper;
    }
}
