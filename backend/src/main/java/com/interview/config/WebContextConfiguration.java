package com.interview.config;

import com.interview.config.converter.InventoryStatusConverter;
import com.interview.config.converter.InventoryTypeConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebContextConfiguration implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new InventoryStatusConverter());
        registry.addConverter(new InventoryTypeConverter());
    }

}
