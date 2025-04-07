package com.hypergeneric.model;

public enum Module {
    BASIC("Basic Features"),
    ADVANCED_QUERY("Advanced Query Tools"),
    DATA_EXPORT("Data Export"),
    DATA_IMPORT("Data Import"),
    SCHEMA_MANAGER("Schema Manager"),
    MONITORING("Performance Monitoring"),
    BACKUP("Backup & Recovery"),
    SECURITY("Enhanced Security");

    private final String displayName;

    Module(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 