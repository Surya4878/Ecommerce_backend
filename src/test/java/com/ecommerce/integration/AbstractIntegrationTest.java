package com.ecommerce.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractIntegrationTest {

    // Use a singleton container to speed up tests
    public static final MySQLContainer<?> mysql =
            new MySQLContainer<>("mysql:8.0.34")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    static {
        mysql.start();
    }

    // This initializer passes JDBC properties to Spring's Environment
    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + mysql.getJdbcUrl(),
                    "spring.datasource.username=" + mysql.getUsername(),
                    "spring.datasource.password=" + mysql.getPassword(),
                    // ensure Hibernate uses the container DB and schema is created
                    "spring.jpa.hibernate.ddl-auto=update",
                    // disable flyway/liquibase if present
                    "spring.flyway.enabled=false",
                    "spring.liquibase.enabled=false",
                    // make sure SQL init runs if you want data.sql
                    "spring.sql.init.mode=always"
            ).applyTo(context.getEnvironment());
        }
    }
}
