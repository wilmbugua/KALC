/*
**    KALC POS  - Professional Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous KALC POS related works   
**
**    https://www.kalc.co.ke
**   
**
*/

package ke.kalc.pos.util;

import java.util.Properties;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConfigurationValidator {
    private Properties properties;

    public ConfigurationValidator(Properties properties) {
        this.properties = properties;
    }

    public void validate() throws ConfigurationException {
        checkRequiredProperties();
        checkDatabaseConnection();
        checkFilePermissions();
        checkLogFilePath();
    }

    private void checkRequiredProperties() throws ConfigurationException {
        String[] requiredProperties = {"db.url", "db.user", "db.password", "log.file.path"};
        StringBuilder missingProps = new StringBuilder();
        for (String prop : requiredProperties) {
            if (properties.getProperty(prop) == null) {
                missingProps.append(prop).append(" ");
            }
        }
        if (missingProps.length() > 0) {
            throw new ConfigurationException("Missing required properties: " + missingProps.toString());
        }
    }

    private void checkDatabaseConnection() throws ConfigurationException {
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            // Successfully connected
        } catch (SQLException e) {
            throw new ConfigurationException("Database connection failed: " + e.getMessage());
        }
    }

    private void checkFilePermissions() throws ConfigurationException {
        String logFilePath = properties.getProperty("log.file.path");
        File logFile = new File(logFilePath);
        try {
            if (!logFile.canWrite()) {
                throw new ConfigurationException("Cannot write to log file: " + logFilePath);
            }
        } catch (Exception e) {
            throw new ConfigurationException("Error checking file permissions: " + e.getMessage());
        }
    }

    private void checkLogFilePath() throws ConfigurationException {
        String logFilePath = properties.getProperty("log.file.path");
        File logFile = new File(logFilePath);
        if (!logFile.getParentFile().exists()) {
            throw new ConfigurationException("Log file path does not exist: " + logFile.getParent());
        }
    }
}