package com.hypergeneric.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@Configuration
@ComponentScan(basePackages = "com.hypergeneric")
@EnableJpaRepositories(basePackages = "com.hypergeneric.repository")
@EntityScan(basePackages = "com.hypergeneric.model")
public class AppConfig {
    // Configuration beans can be added here if needed
}