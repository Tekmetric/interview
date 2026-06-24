package com.interview.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.interview.command.repository",
        entityManagerFactoryRef = "commandEntityManagerFactory",
        transactionManagerRef = "commandTransactionManager"
)
public class CommandDataSourceConfig {

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.command")
    public DataSourceProperties commandDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    public DataSource commandDataSource() {
        return commandDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean commandEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(commandDataSource())
                .packages("com.interview.common.entity", "org.springframework.modulith.events.jpa")
                .persistenceUnit("command")
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager commandTransactionManager(
            @Qualifier("commandEntityManagerFactory") LocalContainerEntityManagerFactoryBean commandEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(commandEntityManagerFactory.getObject()));
    }

    @Bean
    @Profile("!test")  // Don't load data in test profile - tests manage their own data
    public DataSourceInitializer commandDataSourceInitializer() {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(commandDataSource());
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("database/command-schema.sql"));
        populator.addScript(new ClassPathResource("database/command-data.sql"));
        initializer.setDatabasePopulator(populator);
        return initializer;
    }
}
