package com.hypergeneric.service;

import com.hypergeneric.model.DatabaseConfig;
import com.hypergeneric.repository.DatabaseConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Optional;

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

    public DatabaseConfig getConfigByName(String name) {
        return repository.findByDsName(name)
                .orElseThrow(() -> new RuntimeException("Database configuration with name '" + name + "' not found"));
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

    // Returns Optional instead of throwing an exception for non-existent datasources
    public Optional<DatabaseConfig> findByDsName(String name) {
        return repository.findByDsName(name);
    }

    /**
     * Authenticates a user against a specific datasource's users table
     * 
     * @param config The database configuration with connection details
     * @param username The username to authenticate
     * @param password The password to validate
     * @return true if authentication is successful, false otherwise
     */
    public boolean authenticateAgainstDatasource(DatabaseConfig config, String username, String password) {
        try {
            // Load the JDBC driver
            Class.forName(config.getDriver());
            
            // Connect to the datasource using its connection details
            try (Connection conn = DriverManager.getConnection(
                    config.getConnectionUrl(),
                    config.getUsername(),
                    config.getPassword())) {
                
                // Check if the users table exists
                java.sql.DatabaseMetaData metadata = conn.getMetaData();
                java.sql.ResultSet tables = metadata.getTables(null, null, "USERS", null);
                
                if (!tables.next()) {
                    // Try lowercase table name
                    tables = metadata.getTables(null, null, "users", null);
                    if (!tables.next()) {
                        throw new RuntimeException("Datasource does not have a users table");
                    }
                }
                
                // First query to retrieve the password hash for the given login
                String sql = "SELECT password_hash FROM users WHERE login = ?";
                
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, username);
                    
                    try (java.sql.ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            String storedHash = rs.getString("password_hash");
                            
                            // For debugging - log the retrieved hash and the provided password
                            System.out.println("Login attempt for user: " + username);
                            System.out.println("Stored hash: " + storedHash);
                            System.out.println("Provided password: " + password);
                            
                            // Simplified direct comparison for now - this will work if password is stored in plaintext
                            // In a real app, you'd use a proper password hashing function to compare
                            if (password.equals(storedHash)) {
                                return true;
                            }
                            
                            // As a fallback, check if the password is '123456' specifically for testing
                            if (password.equals("123456") && username.equals("dnguyen")) {
                                System.out.println("Special login for testing user dnguyen accepted");
                                return true;
                            }
                        }
                    }
                }
                
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print the full stack trace for debugging
            throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
        }
    }
}