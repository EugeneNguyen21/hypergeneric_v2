package com.hypergeneric.service;

import com.hypergeneric.model.DatabaseConfig;
import com.hypergeneric.model.DatasourceUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DatasourceAuthenticationServiceTest {

    @Mock
    private DatabaseConfigService databaseConfigService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private DatasourceAuthenticationService authService;

    @BeforeEach
    void setUp() {
        authService = new DatasourceAuthenticationService(databaseConfigService, passwordEncoder);
    }

    @Test
    void authenticateAgent_ValidCredentials_ReturnsUserData() {
        // Arrange
        String login = "testUser";
        String password = "testPass";
        String datasourceName = "testDB";
        
        DatabaseConfig config = new DatabaseConfig();
        config.setDsName(datasourceName);
        
        when(databaseConfigService.getConfigByName(datasourceName)).thenReturn(config);
        when(passwordEncoder.matches(password, config.getPassword())).thenReturn(true);

        // Act
        Optional<Map<String, String>> result = authService.authenticateAgent(login, password, datasourceName);

        // Assert
        assertTrue(result.isPresent());
        Map<String, String> userData = result.get();
        assertEquals(login, userData.get("login"));
        verify(passwordEncoder).matches(password, config.getPassword());
    }

    @Test
    void authenticateAgent_InvalidCredentials_ReturnsEmpty() {
        // Arrange
        String login = "wrongUser";
        String password = "wrongPass";
        String datasourceName = "testDB";
        
        DatabaseConfig config = new DatabaseConfig();
        config.setDsName(datasourceName);
        
        when(databaseConfigService.getConfigByName(datasourceName)).thenReturn(config);
        when(passwordEncoder.matches(password, config.getPassword())).thenReturn(false);

        // Act
        Optional<Map<String, String>> result = authService.authenticateAgent(login, password, datasourceName);

        // Assert
        assertTrue(result.isEmpty());
        verify(passwordEncoder).matches(password, config.getPassword());
    }

    @Test
    void authenticateAgent_DatabaseError_ReturnsEmpty() {
        // Arrange
        String login = "testUser";
        String password = "testPass";
        String datasourceName = "invalidDB";
        
        when(databaseConfigService.getConfigByName(datasourceName))
            .thenThrow(new RuntimeException("Database not found"));

        // Act
        Optional<Map<String, String>> result = authService.authenticateAgent(login, password, datasourceName);

        // Assert
        assertTrue(result.isEmpty());
        verify(databaseConfigService).getConfigByName(datasourceName);
        verifyNoInteractions(passwordEncoder);
    }
}