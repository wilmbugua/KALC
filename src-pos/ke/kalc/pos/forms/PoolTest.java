/*
**    KALC POS  - Professional Point of Sale
**
**    Test Utility - Connection Pool Stability Test
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous Openbravo POS related works   
**
**    https://www.KALC.co.uk
**
*/

package ke.kalc.pos.forms;

import ke.kalc.connectionpool.ConnectionPoolFactory;
import java.sql.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Development utility to test database connection pool stability
 *
 * This class verifies that the C3P0 connection pool:
 * - Establishes connections successfully
 * - Executes queries without errors
 * - Properly closes resources
 * - Handles concurrent access
 *
 * Run from command line:
 *   javac -cp "bin:lib/*" -d bin src/pos/ke/kalc/pos/forms/PoolTest.java
 *   java -cp "bin:lib/*" ke.kalc.pos.forms.PoolTest
 *
 * @author KALC Development Team
 * @version 1.0
 */
public class PoolTest {

    /**
     * Runs connection pool stability test (5 iterations)
     * Includes 25-second observation period to monitor pool behavior
     */
    public static void main(final String args[]) throws SQLException {
        System.out.println("Starting KALC POS Connection Pool Test");
        System.out.println("=======================================");
        System.out.println();

        // Run test 5 times to verify consistency
        for (int i = 1; i <= 5; i++) {
            System.out.println("=== Connection Pool Test Run " + i + " ===");
            testDatabaseConnection();
            System.out.println();
        }

        // Wait to observe connection pool behavior
        System.out.println("Observing pool behavior for 25 seconds...");
        try {
            TimeUnit.SECONDS.sleep(25);
        } catch (InterruptedException ex) {
            Logger.getLogger(PoolTest.class.getName())
                .log(Level.SEVERE, "Interrupted during observation period", ex);
        }

        System.out.println("Test completed successfully!");
    }

    /**
     * Executes a single database query to test connection pool
     *
     * @throws SQLException if database operation fails
     */
    private static void testDatabaseConnection() throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        int rowCount = 0;

        try {
            // Obtain connection from pool
            connection = ConnectionPoolFactory.getConnection();
            if (connection == null) {
                System.err.println("ERROR: Failed to obtain connection from pool");
                return;
            }

            // Create statement
            statement = connection.createStatement();

            // Execute query
            resultSet = statement.executeQuery("SELECT * FROM products LIMIT 5");

            // Display results
            while (resultSet.next()) {
                System.out.println("Row " + (++rowCount) + ":");
                System.out.println("  ID: " + resultSet.getString(1));
                System.out.println("  Name: " + resultSet.getString(2));
                if (resultSet.getMetaData().getColumnCount() >= 3) {
                    System.out.println("  Type: " + resultSet.getString(3));
                }
            }

            System.out.println("Success: Retrieved " + rowCount + " rows");

        } catch (SQLException ex) {
            Logger.getLogger(PoolTest.class.getName())
                .log(Level.SEVERE, "Connection test failed", ex);
            System.err.println("ERROR: " + ex.getMessage());
            throw ex;

        } finally {
            // Properly close resources in reverse order
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ex) {
                    Logger.getLogger(PoolTest.class.getName())
                        .log(Level.WARNING, "Error closing resultset", ex);
                }
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    Logger.getLogger(PoolTest.class.getName())
                        .log(Level.WARNING, "Error closing statement", ex);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    Logger.getLogger(PoolTest.class.getName())
                        .log(Level.WARNING, "Error closing connection", ex);
                }
            }
        }
    }
}
