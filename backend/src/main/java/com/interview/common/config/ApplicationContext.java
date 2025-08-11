package com.interview.common.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.interview.jpa.entity.User;
import com.interview.jpa.entity.enums.UserEnum;
import com.interview.jpa.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Configuration
public class ApplicationContext {

    @Primary
    @Scope(SCOPE_SINGLETON)
    @Bean({"applicationObjectMapper", "objectMapper"})
    public ObjectMapper getApplicationObjectMapper() {
        ObjectMapper objectMapper = JsonMapper.builder()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
                .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false)
                .build();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper;
    }

    @Bean
    CommandLineRunner seed(UserRepository repo, PasswordEncoder enc) {
        return args -> repo.findByUsername("admin")
                .orElseGet(() -> repo.save(
                                   User.builder().username("admin")
                                           .email("admin@example.com")
                                           .fullName("Admin")
                                           .role(UserEnum.Role.ADMIN)
                                           .status(UserEnum.Status.ACTIVE)
                                           .passwordHash(enc.encode("admin123"))
                                           .build()
                           )
                );
    }
}
