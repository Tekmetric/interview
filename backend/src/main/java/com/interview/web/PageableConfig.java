package com.interview.web;

import com.interview.common.Pages;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class PageableConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        final var resolver = new PageableHandlerMethodArgumentResolver();
        resolver.setMaxPageSize(1000);
        resolver.setFallbackPageable(Pages.defaultPage());
        resolvers.add(resolver);
    }
}
