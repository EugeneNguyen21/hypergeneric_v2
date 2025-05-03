package com.hypergeneric.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbDebugger implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("=== DATABASE DEBUG INFO ===");
        
        // Get all tables in the database
        List<String> tables = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'", 
                String.class);
        
        log.info("Found {} tables in database", tables.size());
        
        // For each table, get the row count and some sample data
        for (String table : tables) {
            try {
                Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class);
                log.info("Table: {} | Row count: {}", table, count);
                
                if (count > 0) {
                    List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM " + table + " LIMIT 10");
                    log.info("Sample data from {}: {}", table, rows);
                }
            } catch (Exception e) {
                log.error("Error querying table {}: {}", table, e.getMessage());
            }
        }
        
        // Specific check for database_configs table
        try {
            List<Map<String, Object>> configs = jdbcTemplate.queryForList("SELECT * FROM database_configs");
            log.info("database_configs contents: {}", configs);
        } catch (Exception e) {
            log.error("Error querying database_configs table: {}", e.getMessage());
        }
        
        log.info("=== END DATABASE DEBUG INFO ===");
    }
}