package com.hypergeneric.model;

import javax.persistence.*;
import lombok.Data;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.List;

@Data
@Entity
@Table(name = "database_configs")
public class DatabaseConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ds_name", nullable = false)
    private String dsName;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String driver;

    @Column(name = "db_type", nullable = false)
    private String dbType;

    @Column(name = "connection_url", nullable = false, length = 500)
    private String connectionUrl;

    @Column(name = "license", nullable = true, length = 1000)
    private String license;

    @Transient
    @JsonProperty("enabledModules")
    private Set<Module> enabledModules = new HashSet<>();

    @JsonIgnore
    @PostLoad
    public void decryptLicense() {
        if (license != null && !license.isEmpty()) {
            try {
                // TODO: Implement proper decryption here
                String decrypted = license; // Placeholder for decryption
                enabledModules = Arrays.stream(decrypted.split(","))
                    .map(String::trim)
                    .map(Module::valueOf)
                    .collect(Collectors.toSet());
            } catch (Exception e) {
                enabledModules = new HashSet<>();
            }
        }
    }

    @JsonIgnore
    @PrePersist
    @PreUpdate
    public void encryptLicense() {
        if (enabledModules != null && !enabledModules.isEmpty()) {
            try {
                // TODO: Implement proper encryption here
                String modulesString = enabledModules.stream()
                    .map(Module::name)
                    .collect(Collectors.joining(","));
                license = modulesString; // Placeholder for encryption
            } catch (Exception e) {
                license = "";
            }
        } else {
            license = "";
        }
    }

    @JsonSetter("enabledModules")
    public void setEnabledModules(List<String> moduleNames) {
        if (moduleNames != null) {
            enabledModules = moduleNames.stream()
                .map(Module::valueOf)
                .collect(Collectors.toSet());
        } else {
            enabledModules = new HashSet<>();
        }
    }

    public boolean hasModule(Module module) {
        return enabledModules != null && enabledModules.contains(module);
    }
} 