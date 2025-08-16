package com.interview.config;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// Group related configs of Customer in a centralized file here,
// which supports type-safe binding
@Component
@ConfigurationProperties(prefix = "customer")
@Getter
@Setter
public class CustomerConfig {
    // Defined in application.properties
    // @Min(3): validate the threshold must >= 3
    @Min(3)
    private int threshold;
}
