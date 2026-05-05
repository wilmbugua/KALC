package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * Main application class for KALCPOS (Kalc Point of Sale System).
 * Demonstrates database operations using SessionFactory and DbUtils.
 */
public class KALCPOSApplication {
    private static final Logger logger = LoggerFactory.getLogger(KALCPOSApplication.class);
    
    // SQL Constants
    private static final String SELECT_QUERY = "SELECT * FROM product_line";
    private static final String INSERT_QUERY = "INSERT INTO product_line (name, description) VALUES (?, ?)";
    private static final String SELECT_BY_NAME = "SELECT * FROM product_line WHERE name = ?";
    private static final String UPDATE_QUERY = "UPDATE product_line SET description = ? WHERE name = ?";
    private static final String DELETE_QUERY = "DELETE FROM product_line WHERE name = ?";
    
    private final SessionFactory sessionFactory;
    
    /**
     * Constructor initializes the SessionFactory
     */
    public KALCPOSApplication() {
        this.sessionFactory = SessionFactory.getInstance();
    }
    
    /**
     * Inserts a new product line using DbUtils
     * @param name the name of the product line
     * @param description the description of the product line
     * @return number of rows affected
     */
    public int insertProductLine(String name, String description) {
        int rowsAffected = DbUtils.executeUpdate(INSERT_QUERY, name, description);
        logger.info("Inserted {} row(s) for product line: {}", rowsAffected, name);
        return rowsAffected;
    }
    
    /**
     * Retrieves all product lines using DbUtils
     */
    public void listProductLines() {
        logger.info("Retrieving all product lines from database...");
        
        try (Connection connection = sessionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_QUERY);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            
            logger.info("Product Lines:");
            logger.info("─────────────────────────────────────────");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                Timestamp createdAt = resultSet.getTimestamp("created_at");
                logger.info("  ID: {} | Name: {:15} | Description: {:30} | Created: {}", 
                    id, name, description, createdAt);
            }
            logger.info("─────────────────────────────────────────");
            
        } catch (DatabaseConnectionException e) {
            logger.error("Failed to obtain database connection: {}", e.getMessage());
        } catch (SQLException e) {
            logger.error("SQL Exception occurred: {}", e.getMessage());
            logger.error("SQL State: {}, Error Code: {}", e.getSQLState(), e.getErrorCode());
        }
    }
    
    /**
     * Retrieves all product lines using DbUtils utility
     */
    public void listProductLinesUsingDbUtils() {
        logger.info("Retrieving all product lines using DbUtils...");
        List<Map<String, Object>> results = DbUtils.executeQuery(SELECT_QUERY);
        
        if (results.isEmpty()) {
            logger.info("No product lines found in database.");
            return;
        }
        
        logger.info("Product Lines (using DbUtils):");
        logger.info("─────────────────────────────────────────");
        for (Map<String, Object> row : results) {
            logger.info("  ID: {} | Name: {:15} | Description: {:30} | Created: {}", 
                row.get("id"), row.get("name"), row.get("description"), row.get("created_at"));
        }
        logger.info("─────────────────────────────────────────");
    }
    
    /**
     * Finds a product line by name using DbUtils
     * @param name the name to search for
     */
    public void findProductLineByName(String name) {
        logger.info("Searching for product line: '{}'", name);
        List<Map<String, Object>> results = DbUtils.executeQuery(SELECT_BY_NAME, name);
        
        if (results.isEmpty()) {
            logger.info("No product line found with name: '{}'", name);
        } else {
            for (Map<String, Object> row : results) {
                logger.info("  Found: ID: {} | Name: {} | Description: {} | Created: {}", 
                    row.get("id"), row.get("name"), row.get("description"), row.get("created_at"));
            }
        }
    }
    
    /**
     * Updates a product line description using DbUtils
     * @param name the product line name to update
     * @param newDescription the new description
     * @return number of rows affected
     */
    public int updateProductLineDescription(String name, String newDescription) {
        int rowsAffected = DbUtils.executeUpdate(UPDATE_QUERY, newDescription, name);
        logger.info("Updated {} row(s) for product line: '{}'", rowsAffected, name);
        return rowsAffected;
    }
    
    /**
     * Deletes a product line using DbUtils
     * @param name the product line name to delete
     * @return number of rows affected
     */
    public int deleteProductLine(String name) {
        int rowsAffected = DbUtils.executeUpdate(DELETE_QUERY, name);
        logger.info("Deleted {} row(s) for product line: '{}'", rowsAffected, name);
        return rowsAffected;
    }
    
    /**
     * Demonstrates batch operations
     */
    public void demonstrateBatchOperations() {
        logger.info("Demonstrating batch insert operations...");
        
        String batchInsert = "INSERT INTO product_line (name, description) VALUES (?, ?)";
        List<Object[]> batchParameters = List.of(
            new Object[]{"Beverages", "Soft drinks and beverages"},
            new Object[]{"Snacks", "Chips and snack items"},
            new Object[]{"Household", "Cleaning and household items"}
        );
        
        int[] results = DbUtils.executeBatch(batchInsert, batchParameters);
        logger.info("Batch insert completed. Rows affected per query: {}", 
            java.util.Arrays.toString(results));
    }
    
    /**
     * Main method - entry point of the application
     * @param args command line arguments
     */
    public static void main(String[] args) {
        KALCPOSApplication app = new KALCPOSApplication();
        
        // Test database connection
        logger.info("════════════════════════════════════════════");
        logger.info("  KALCPOS - Point of Sale System");
        logger.info("════════════════════════════════════════════");
        logger.info("Testing database connection...");
        
        SessionFactory factory = SessionFactory.getInstance();
        if (factory.testConnection()) {
            logger.info("✓ Database connection test passed!\n");
            
            // Demonstrate various operations
            logger.info("--- 1. Inserting sample product lines ---");
            app.insertProductLine("Electronics", "Electronic devices and accessories");
            app.insertProductLine("Clothing", "Apparel and fashion items");
            app.insertProductLine("Food", "Grocery and food products");
            
            logger.info("\n--- 2. Listing all product lines (manual) ---");
            app.listProductLines();
            
            logger.info("\n--- 3. Listing all product lines (DbUtils) ---");
            app.listProductLinesUsingDbUtils();
            
            logger.info("\n--- 4. Searching for specific product line ---");
            app.findProductLineByName("Electronics");
            
            logger.info("\n--- 5. Updating product line description ---");
            app.updateProductLineDescription("Clothing", "Apparel, fashion items, and accessories");
            
            logger.info("\n--- 6. Demonstrating batch operations ---");
            app.demonstrateBatchOperations();
            
            logger.info("\n--- 7. Final product line listing ---");
            app.listProductLinesUsingDbUtils();
            
            logger.info("\n--- 8. Checking table statistics ---");
            long rowCount = DbUtils.getRowCount("product_line");
            logger.info("Total rows in product_line table: {}", rowCount);
            logger.info("Table exists: {}", DbUtils.tableExists("product_line"));
            
            logger.info("\n════════════════════════════════════════════");
            logger.info("  All operations completed successfully!");
            logger.info("════════════════════════════════════════════");
        } else {
            logger.error("✗ Database connection test failed. Please check your database configuration.");
        }
    }
}