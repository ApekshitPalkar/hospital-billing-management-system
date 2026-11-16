package com.apekshit.hospital.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/** Reads local database settings without storing credentials in source code. */
public final class DatabaseConfig {
    private final String url;
    private final String user;
    private final String password;

    private DatabaseConfig(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public static DatabaseConfig load() {
        Properties properties = new Properties();
        Path path = Paths.get("database.properties");
        if (path.toFile().isFile()) {
            try (FileInputStream input = new FileInputStream(path.toFile())) {
                properties.load(input);
            } catch (IOException ex) {
                System.err.println("Could not read database.properties: " + ex.getMessage());
            }
        }
        String url = value("HOSPITAL_DB_URL", properties.getProperty("db.url",
                "jdbc:mariadb://127.0.0.1:3306/hospital_billing_db?sslMode=disable"));
        String user = value("HOSPITAL_DB_USER", properties.getProperty("db.user", ""));
        String password = value("HOSPITAL_DB_PASSWORD", properties.getProperty("db.password", ""));
        return new DatabaseConfig(url, user, password);
    }

    private static String value(String environmentName, String fallback) {
        String environmentValue = System.getenv(environmentName);
        return environmentValue == null || environmentValue.trim().isEmpty() ? fallback : environmentValue;
    }

    public boolean isConfigured() {
        return url != null && !url.trim().isEmpty()
                && user != null && !user.trim().isEmpty()
                && password != null && !password.trim().isEmpty()
                && !"CHANGE_THIS_PASSWORD".equals(password);
    }

    public String getUrl() { return url; }
    public String getUser() { return user; }
    public String getPassword() { return password; }
}
