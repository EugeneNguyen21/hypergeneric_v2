package com.hypergeneric.service;

import com.hypergeneric.model.DatabaseConfig;
import com.hypergeneric.repository.DatabaseConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DatabaseConfigService {
    private final DatabaseConfigRepository repository;

    public List<DatabaseConfig> getAllConfigs() {
        return repository.findAll();
    }

    public DatabaseConfig getConfigById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Database configuration not found"));
    }

    public DatabaseConfig saveConfig(DatabaseConfig config) {
        return repository.save(config);
    }

    public void deleteConfig(Long id) {
        repository.deleteById(id);
    }

    public boolean testConnection(DatabaseConfig config) {
        try {
            Class.forName(config.getDriver());
            try (Connection conn = DriverManager.getConnection(
                    config.getConnectionUrl(),
                    config.getUsername(),
                    config.getPassword())) {
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException("Connection failed: " + e.getMessage(), e);
        }
    }
} 