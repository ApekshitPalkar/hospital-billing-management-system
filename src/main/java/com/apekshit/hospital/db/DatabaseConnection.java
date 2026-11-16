package com.apekshit.hospital.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private final DatabaseConfig config;

    public DatabaseConnection(DatabaseConfig config) {
        this.config = config;
    }

    public Connection open() throws SQLException {
        if (!config.isConfigured()) {
            throw new SQLException("Database credentials are not configured. Create database.properties first.");
        }
        return DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
    }
}
