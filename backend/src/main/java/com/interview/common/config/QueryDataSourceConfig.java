package com.interview.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        basePackages = "com.interview.query.repository",
        entityManagerFactoryRef = "queryEntityManagerFactory",
        transactionManagerRef = "queryTransactionManager"
)
public class QueryDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.query")
    public DataSourceProperties queryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource queryDataSource() {
        return queryDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean queryEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(queryDataSource())
                .packages("com.interview.common.entity")
                .persistenceUnit("query")
                .build();
    }

    @Bean
    public PlatformTransactionManager queryTransactionManager(
            @Qualifier("queryEntityManagerFactory") LocalContainerEntityManagerFactoryBean queryEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(queryEntityManagerFactory.getObject()));
    }

    @Bean
    public DataSourceInitializer queryDataSourceInitializer() {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(queryDataSource());
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("database/query-schema.sql"));
        populator.addScript(new ClassPathResource("database/query-data.sql"));
        initializer.setDatabasePopulator(populator);
        return initializer;
    }
}
