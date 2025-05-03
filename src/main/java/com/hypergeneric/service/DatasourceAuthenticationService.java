package com.hypergeneric.service;

import com.hypergeneric.model.DatabaseConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DatasourceAuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(DatasourceAuthenticationService.class);
    private final DatabaseConfigService databaseConfigService;
    private final PasswordEncoder passwordEncoder;

    public Optional<Map<String, String>> authenticateAgent(String login, String password, String datasourceName) {
        try {
            // Fetch datasource config
            DatabaseConfig config = databaseConfigService.getConfigByName(datasourceName);
            Class.forName(config.getDriver());
            try (Connection conn = DriverManager.getConnection(
                    config.getConnectionUrl(),
                    config.getUsername(),
                    config.getPassword())) {
                String sql = "SELECT user_id, login, password_hash FROM users WHERE login = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, login);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            String dbHash = rs.getString("password_hash");
                            if (passwordEncoder.matches(password, dbHash)) {
                                Map<String, String> userData = new HashMap<>();
                                userData.put("id", rs.getString("user_id"));
                                userData.put("login", rs.getString("login"));
                                return Optional.of(userData);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Authentication failed for user '{}' on datasource '{}': {}", 
                login, datasourceName, e.getMessage());
        }
        return Optional.empty();
    }
}