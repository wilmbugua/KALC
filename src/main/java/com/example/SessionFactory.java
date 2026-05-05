package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * SessionFactory manages database connections for the KALCPOS application.
 * Provides a centralized way to create and manage database connections
 * with proper error handling and logging.
 */
public class SessionFactory {
    private static final Logger logger = LoggerFactory.getLogger(SessionFactory.class);
    
    // Database configuration - should be loaded from a configuration file or environment variables
    private static final String DB_URL = "jdbc:mysql://localhost:3306/kalcpo_db";
    private static final String DB_USER = "your_username";
    private static final String DB_PASSWORD = "your_password";
    
    // Singleton instance
    private static SessionFactory instance;
    
    /**
     * Private constructor to prevent instantiation
     */
    private SessionFactory() {
        logger.info("SessionFactory initialized");
    }
    
    /**
     * Get the singleton instance of SessionFactory
     * @return SessionFactory instance
     */
    public static synchronized SessionFactory getInstance() {
        if (instance == null) {
            instance = new SessionFactory();
        }
        return instance;
    }
    
    /**
     * Establish a database connection
     * @return Connection object
     * @throws DatabaseConnectionException if connection fails
     */
    public Connection getConnection() throws DatabaseConnectionException {
        try {
            // Attempt to establish a database connection
            logger.debug("Attempting to establish database connection to: {}", DB_URL);
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            logger.info("Database connection established successfully");
            return connection;
        } catch (SQLException e) {
            logger.error("Database connection failed: {}", e.getMessage());
            logger.error("SQL State: {}, Error Code: {}", e.getSQLState(), e.getErrorCode());
            throw new DatabaseConnectionException("Unable to connect to the database.", e);
        }
    }
    
    /**
     * Test the database connection
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection() {
        try (Connection connection = getConnection()) {
            if (connection != null && !connection.isClosed()) {
                logger.info("Database connection test successful");
                return true;
            }
        } catch (DatabaseConnectionException | SQLException e) {
            logger.error("Database connection test failed: {}", e.getMessage());
        }
        return false;
    }
    
    /**
     * Close a connection safely
     * @param connection the connection to close
     */
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                logger.debug("Database connection closed");
            } catch (SQLException e) {
                logger.error("Failed to close database connection: {}", e.getMessage());
            }
        }
    }
}