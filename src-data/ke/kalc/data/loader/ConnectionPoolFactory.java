/*
**    KALCPOS  - Professional Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous KALC POS related works    
**
**    https://www.kalc.co.ke
**   
*/



package ke.kalc.data.loader;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author
 */
public class ConnectionPoolFactory {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionPoolFactory.class);
    
    private BasicDataSource dataSource;
    
    private static ConnectionPoolFactory INSTANCE = new ConnectionPoolFactory();
    
    private ConnectionPoolFactory() {
        initializeDataSource();
    }
    
    public static ConnectionPoolFactory getInstance() {
        return INSTANCE;
    }
    
    private void initializeDataSource() {
        try {
            // Configure connection pool settings
            dataSource = new BasicDataSource();
            
            // Load database configuration from AppConfig
            String driverClass = ke.kalc.pos.forms.AppConfig.getDatabaseProviderClass();
            String url = ke.kalc.pos.forms.AppConfig.getDatabaseURL();
            String user = ke.kalc.pos.forms.AppConfig.getDatabaseUser();
            String password = ke.kalc.pos.forms.AppConfig.getClearDatabasePassword();
            
            dataSource.setDriverClassName(driverClass);
            dataSource.setUrl(url);
            dataSource.setUsername(user);
            dataSource.setPassword(password);
            
            // Connection pool settings
            dataSource.setMaxIdleTime(300); // Max idle time in seconds
            dataSource.setMaxWaitMillis(30000); // Timeout for checking out a connection in milliseconds
            dataSource.setTimeBetweenEvictionRunsMillis(300000); // Idle connection test period in milliseconds (5 minutes)
            dataSource.setTestOnBorrow(true); // Validate connection on borrow
            dataSource.setValidationQuery("SELECT 1"); // Simple validation query
            dataSource.setMinIdle(5); // Minimum number of idle connections
            dataSource.setMaxTotal(50); // Maximum number of active connections
            
            logger.info("Connection pool initialized successfully");
            logger.info("Database URL: {}", url);
            logger.info("Max total connections: {}", dataSource.getMaxTotal());
            logger.info("Min idle connections: {}", dataSource.getMinIdle());
        } catch (Exception e) {
            logger.error("Error initializing connection pool", e);
            throw new RuntimeException("Could not initialize connection pool", e);
        }
    }
    
    public Connection getConnection() {
        try {
            Connection conn = dataSource.getConnection();
            logger.debug("Connection borrowed from pool. Active: {}, Idle: {}", 
                dataSource.getNumActive(), dataSource.getNumIdle());
            return conn;
        } catch (SQLException e) {
            logger.error("Error getting connection from pool", e);
            throw new RuntimeException("Could not get connection from pool", e);
        }
    }
    
    public void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                logger.debug("Connection returned to pool. Active: {}, Idle: {}", 
                    dataSource.getNumActive(), dataSource.getNumIdle());
            } catch (SQLException e) {
                logger.error("Error releasing connection", e);
            }
        }
    }
    
    // Add logging for pool metrics
    public void logMetrics() {
        if (dataSource != null) {
            logger.info("=== Connection Pool Metrics ===");
            logger.info("Active connections: {}", dataSource.getNumActive());
            logger.info("Idle connections: {}", dataSource.getNumIdle());
            logger.info("Max total connections: {}", dataSource.getMaxTotal());
            logger.info("Min idle connections: {}", dataSource.getMinIdle());
            logger.info("Max idle connections: {}", dataSource.getMaxIdle());
            logger.info("Total connections created: {}", dataSource.getCreatedCount());
            logger.info("Connections closed: {}", dataSource.getClosedCount());
            logger.info("===============================");
        }
    }
    
    public void closePool() {
        try {
            if (dataSource != null) {
                dataSource.close();
                logger.info("Connection pool closed");
            }
        } catch (SQLException e) {
            logger.error("Error closing connection pool", e);
        }
    }
    
    public boolean isPoolActive() {
        return dataSource != null && !dataSource.isClosed();
    }
}
