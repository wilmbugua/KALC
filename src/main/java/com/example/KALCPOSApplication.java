package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;

/**
 * Main application class for KALCPOS (Kalc Point of Sale System).
 * Demonstrates database operations using the SessionFactory for connection management.
 */
public class KALCPOSApplication {
    private static final Logger logger = LoggerFactory.getLogger(KALCPOSApplication.class);
    
    // SQL Constants
    private static final String SELECT_QUERY = "SELECT * FROM product_line";
    private static final String INSERT_QUERY = "INSERT INTO product_line (name, description) VALUES (?, ?)";
    
    private final SessionFactory sessionFactory;
    
    /**
     * Constructor initializes the SessionFactory
     */
    public KALCPOSApplication() {
        this.sessionFactory = SessionFactory.getInstance();
    }
    
    /**
     * Inserts a new product line into the database
     * @param name the name of the product line
     * @param description the description of the product line
     */
    public void insertProductLine(String name, String description) {
        // Using try-with-resources for connection and statement
        try (Connection connection = sessionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY)) {
            
            // Set parameters and execute
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            int rowsAffected = preparedStatement.executeUpdate();
            
            logger.info("Successfully inserted {} row(s) into product_line table", rowsAffected);
            
        } catch (DatabaseConnectionException e) {
            logger.error("Failed to obtain database connection: {}", e.getMessage());
        } catch (SQLException e) {
            logger.error("SQL Exception occurred: {}", e.getMessage());
            logger.error("SQL State: {}, Error Code: {}", e.getSQLState(), e.getErrorCode());
        }
        // No need for finally block as resources are auto-closed
    }
    
    /**
     * Retrieves all product lines from the database
     */
    public void listProductLines() {
        try (Connection connection = sessionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_QUERY);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            
            logger.info("Product Lines:");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                logger.info("  ID: {}, Name: {}, Description: {}", id, name, description);
            }
            
        } catch (DatabaseConnectionException e) {
            logger.error("Failed to obtain database connection: {}", e.getMessage());
        } catch (SQLException e) {
            logger.error("SQL Exception occurred: {}", e.getMessage());
            logger.error("SQL State: {}, Error Code: {}", e.getSQLState(), e.getErrorCode());
        }
    }
    
    /**
     * Main method - entry point of the application
     * @param args command line arguments
     */
    public static void main(String[] args) {
        KALCPOSApplication app = new KALCPOSApplication();
        
        // Test database connection
        logger.info("Testing database connection...");
        SessionFactory factory = SessionFactory.getInstance();
        if (factory.testConnection()) {
            logger.info("Database connection test passed!");
            
            // Insert sample data
            app.insertProductLine("Electronics", "Electronic devices and accessories");
            app.insertProductLine("Clothing", "Apparel and fashion items");
            app.insertProductLine("Food", "Grocery and food products");
            
            // List all product lines
            app.listProductLines();
        } else {
            logger.error("Database connection test failed. Please check your database configuration.");
        }
    }
}