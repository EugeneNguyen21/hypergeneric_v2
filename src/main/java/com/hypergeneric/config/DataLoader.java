package com.hypergeneric.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class DataLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            // Check if database_configs table is empty
            int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM database_configs", Integer.class);
            
            if (count == 0) {
                logger.info("No datasource configurations found. Loading sample data...");
                try {
                    // Load and execute sample_datasources.sql
                    ClassPathResource resource = new ClassPathResource("sample_datasources.sql");
                    Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
                    String sql = FileCopyUtils.copyToString(reader);
                    
                    // Split and execute each SQL statement
                    for (String statement : sql.split(";")) {
                        if (!statement.trim().isEmpty()) {
                            jdbcTemplate.execute(statement);
                        }
                    }
                    
                    logger.info("Sample datasource configurations loaded successfully");
                } catch (Exception e) {
                    logger.error("Error loading sample data: {}", e.getMessage(), e);
                }
            } else {
                logger.info("Found {} existing datasource configurations", count);
            }
        };
    }
}