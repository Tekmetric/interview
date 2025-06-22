package com.interview;

import org.flywaydb.core.Flyway;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;

@Testcontainers
public abstract class AbstractIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:14-alpine")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");

    static {
        postgreSQLContainer.start();

        Flyway.configure()
              .dataSource(postgreSQLContainer.getJdbcUrl(),
                          postgreSQLContainer.getUsername(),
                          postgreSQLContainer.getPassword())
              .locations("classpath:db/migration")
              .load()
              .migrate();
    }

    @DynamicPropertySource
    public static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
