package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DbUtils provides utility methods for common database operations.
 * Includes methods for executing queries, updates, and retrieving results.
 */
public class DbUtils {
    private static final Logger logger = LoggerFactory.getLogger(DbUtils.class);
    
    /**
     * Private constructor to prevent instantiation
     */
    private DbUtils() {
        throw new UnsupportedOperationException("DbUtils is a utility class and cannot be instantiated");
    }
    
    /**
     * Executes a SELECT query and returns the ResultSet as a list of maps
     * @param query the SQL SELECT query to execute
     * @return list of maps representing rows (column name -> value)
     */
    public static List<Map<String, Object>> executeQuery(String query) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Connection connection = SessionFactory.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    row.put(columnName, value);
                }
                results.add(row);
            }
            
            logger.info("Query executed successfully. Rows returned: {}", results.size());
            logger.debug("Query: {}", query);
            
        } catch (DatabaseConnectionException e) {
            logger.error("Failed to obtain database connection while executing query: {}", query, e);
        } catch (SQLException e) {
            logger.error("SQL error while executing query: {}", query, e);
            logger.error("SQL State: {}, Error Code: {}", getSqlState(e), e.getErrorCode());
        } catch (Exception e) {
            logger.error("Unexpected error occurred while executing query: {}", query, e);
        }
        
        return results;
    }
    
    /**
     * Executes a SELECT query with parameters and returns the ResultSet as a list of maps
     * @param query the SQL SELECT query with placeholders
     * @param parameters the parameters to set in the query
     * @return list of maps representing rows (column name -> value)
     */
    public static List<Map<String, Object>> executeQuery(String query, Object... parameters) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Connection connection = SessionFactory.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            // Set parameters
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }
            
            logger.debug("Executing parameterized query with {} parameters", parameters.length);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object value = resultSet.getObject(i);
                        row.put(columnName, value);
                    }
                    results.add(row);
                }
                
                logger.info("Parameterized query executed successfully. Rows returned: {}", results.size());
            }
            
        } catch (DatabaseConnectionException e) {
            logger.error("Failed to obtain database connection while executing query: {}", query, e);
        } catch (SQLException e) {
            logger.error("SQL error while executing parameterized query: {}", query, e);
            logger.error("SQL State: {}, Error Code: {}", getSqlState(e), e.getErrorCode());
        } catch (Exception e) {
            logger.error("Unexpected error occurred while executing parameterized query: {}", query, e);
        }
        
        return results;
    }
    
    /**
     * Executes an INSERT, UPDATE, or DELETE query
     * @param query the SQL query to execute
     * @param parameters the parameters to set in the query
     * @return number of rows affected
     */
    public static int executeUpdate(String query, Object... parameters) {
        int rowsAffected = 0;
        
        try (Connection connection = SessionFactory.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            // Set parameters
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }
            
            rowsAffected = preparedStatement.executeUpdate();
            logger.info("Update executed successfully. Rows affected: {}", rowsAffected);
            logger.debug("Query: {}", query);
            
        } catch (DatabaseConnectionException e) {
            logger.error("Failed to obtain database connection while executing update: {}", query, e);
        } catch (SQLException e) {
            logger.error("SQL error while executing update: {}", query, e);
            logger.error("SQL State: {}, Error Code: {}", getSqlState(e), e.getErrorCode());
        } catch (Exception e) {
            logger.error("Unexpected error occurred while executing update: {}", query, e);
        }
        
        return rowsAffected;
    }
    
    /**
     * Executes a batch of INSERT, UPDATE, or DELETE queries
     * @param queries list of SQL queries to execute
     * @return array of rows affected for each query
     */
    public static int[] executeBatch(List<String> queries) {
        try (Connection connection = SessionFactory.getInstance().getConnection();
             Statement statement = connection.createStatement()) {
            
            connection.setAutoCommit(false);
            
            for (String query : queries) {
                statement.addBatch(query);
            }
            
            int[] results = statement.executeBatch();
            connection.commit();
            
            logger.info("Batch executed successfully. Queries executed: {}", results.length);
            return results;
            
        } catch (DatabaseConnectionException e) {
            logger.error("Failed to obtain database connection while executing batch", e);
        } catch (SQLException e) {
            logger.error("SQL error while executing batch: {}", e.getMessage(), e);
            logger.error("SQL State: {}, Error Code: {}", getSqlState(e), e.getErrorCode());
        } catch (Exception e) {
            logger.error("Unexpected error occurred while executing batch: {}", e.getMessage(), e);
        }
        
        return new int[0];
    }
    
    /**
     * Executes a batch of INSERT, UPDATE, or DELETE queries with parameters
     * @param query the SQL query template
     * @param parametersList list of parameter arrays for each query execution
     * @return array of rows affected for each query
     */
    public static int[] executeBatch(String query, List<Object[]> parametersList) {
        try (Connection connection = SessionFactory.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            connection.setAutoCommit(false);
            
            for (Object[] parameters : parametersList) {
                // Clear parameters
                for (int i = 0; i < parameters.length; i++) {
                    preparedStatement.setObject(i + 1, parameters[i]);
                }
                preparedStatement.addBatch();
            }
            
            int[] results = preparedStatement.executeBatch();
            connection.commit();
            
            logger.info("Parameterized batch executed successfully. Queries executed: {}", results.length);
            return results;
            
        } catch (DatabaseConnectionException e) {
            logger.error("Failed to obtain database connection while executing parameterized batch", e);
        } catch (SQLException e) {
            logger.error("SQL error while executing parameterized batch: {}", e.getMessage(), e);
            logger.error("SQL State: {}, Error Code: {}", getSqlState(e), e.getErrorCode());
        } catch (Exception e) {
            logger.error("Unexpected error occurred while executing parameterized batch: {}", e.getMessage(), e);
        }
        
        return new int[0];
    }
    
    /**
     * Checks if a table exists in the database
     * @param tableName the name of the table to check
     * @return true if the table exists, false otherwise
     */
    public static boolean tableExists(String tableName) {
        try (Connection connection = SessionFactory.getInstance().getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet resultSet = metaData.getTables(null, null, tableName, null)) {
                boolean exists = resultSet.next();
                logger.debug("Table '{}' exists: {}", tableName, exists);
                return exists;
            }
        } catch (DatabaseConnectionException e) {
            logger.error("Failed to obtain database connection while checking table existence: {}", tableName, e);
        } catch (SQLException e) {
            logger.error("SQL error while checking table existence: {}", tableName, e);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while checking table existence: {}", tableName, e);
        }
        return false;
    }
    
    /**
     * Gets the number of rows in a table
     * @param tableName the name of the table
     * @return number of rows, or -1 if error occurs
     */
    public static long getRowCount(String tableName) {
        String query = "SELECT COUNT(*) FROM " + tableName;
        
        try (Connection connection = SessionFactory.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            
            if (resultSet.next()) {
                long count = resultSet.getLong(1);
                logger.debug("Row count for table '{}': {}", tableName, count);
                return count;
            }
            
        } catch (DatabaseConnectionException e) {
            logger.error("Failed to obtain database connection while counting rows: {}", tableName, e);
        } catch (SQLException e) {
            logger.error("SQL error while counting rows: {}", tableName, e);
            logger.error("SQL State: {}, Error Code: {}", getSqlState(e), e.getErrorCode());
        } catch (Exception e) {
            logger.error("Unexpected error occurred while counting rows: {}", tableName, e);
        }
        
        return -1;
    }
    
    /**
     * Helper method to get SQL state from SQLException
     */
    private static String getSqlState(SQLException e) {
        return e.getSQLState() != null ? e.getSQLState() : "N/A";
    }
    
    /**
     * Closes a ResultSet safely
     */
    public static void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                logger.error("Failed to close ResultSet: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Closes a Statement safely
     */
    public static void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                logger.error("Failed to close Statement: {}", e.getMessage());
            }
        }
    }
}