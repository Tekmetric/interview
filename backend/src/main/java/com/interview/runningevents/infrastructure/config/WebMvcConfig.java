package com.interview.runningevents.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration to enhance error handling.
 * This configuration works in conjunction with application.properties settings
 * to enable throwing NoHandlerFoundException for unmatched paths.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    // This class provides a central place for web-specific configuration
    // The NoHandlerFoundException enablement is done via properties:
    // spring.mvc.throw-exception-if-no-handler-found=true
    // spring.web.resources.add-mappings=false
}
