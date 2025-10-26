package com.usermanagement.user.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@Configuration
public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private final Environment env;

    public DatabaseConfig(Environment env) {
        this.env = env;
    }

    @EventListener(ApplicationStartedEvent.class)
    public void logDatabaseUrl() {
        String dbUrl = env.getProperty("spring.datasource.url");
        logger.info("Database URL: {}", dbUrl);

        // Log other database properties
        logger.info("Database Driver: {}", env.getProperty("spring.datasource.driver-class-name"));
        logger.info("Hibernate DDL Auto: {}", env.getProperty("spring.jpa.hibernate.ddl-auto"));
    }
}
