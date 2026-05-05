package com.example;

import java.sql.*;
import java.util.logging.Logger;

public class KALCPOSApplication {
    private static final Logger logger = Logger.getLogger(KALCPOSApplication.class.getName());

    // Database configuration - update these with your actual database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/kalcpo_db";
    private static final String DB_USER = "your_username";
    private static final String DB_PASSWORD = "your_password";

    // SQL Constants
    private static final String SELECT_QUERY = "SELECT * FROM product_line";
    private static final String INSERT_QUERY = "INSERT INTO product_line (name, description) VALUES (?, ?)";

    public void insertProductLine(String name, String description) {
        // Using try-with-resources for connection and statement
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY)) {
            
            // Set parameters and execute
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            int rowsAffected = preparedStatement.executeUpdate();
            
            logger.info("Successfully inserted " + rowsAffected + " row(s) into product_line table");
            
        } catch (SQLException e) {
            logger.severe("SQL Exception occurred: " + e.getMessage());
            logger.severe("SQL State: " + e.getSQLState());
            logger.severe("Error Code: " + e.getErrorCode());
        }
        // No need for finally block as resources are auto-closed
    }

    public static void main(String[] args) {
        KALCPOSApplication app = new KALCPOSApplication();
        app.insertProductLine("Sample Product", "Sample Description");
    }
}