package com.interview.application.configuration.jakson;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson spring boot configuration bean.
 */
@Configuration
public class JacksonConfiguration {

    @Bean
    @Primary
    public ObjectMapper objectMapperBuilder() {
        ObjectMapper objectMapper = new ObjectMapper();
        SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
        serializationConfig = serializationConfig.with(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
        serializationConfig = serializationConfig.withView(View.Public.class);
        objectMapper.setConfig(serializationConfig);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
