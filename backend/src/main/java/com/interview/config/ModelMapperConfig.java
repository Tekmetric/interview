package com.interview.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Use strict matching strategy to avoid ambiguous mappings
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // Allow skipping null values during mapping
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        return modelMapper;
    }
}
