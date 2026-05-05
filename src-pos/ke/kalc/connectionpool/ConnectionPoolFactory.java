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
package ke.kalc.connectionpool;

import java.sql.Connection;
import java.sql.SQLException;
import ke.kalc.pos.forms.AppConfig;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 *
 * @author John Lewis
 */
public class ConnectionPoolFactory {

    //  private final static ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
    public static ComboPooledDataSource comboPooledDataSource;

    static {
        comboPooledDataSource = new ComboPooledDataSource();

        comboPooledDataSource.setJdbcUrl(AppConfig.getDatabaseURL());
        comboPooledDataSource.setUser(AppConfig.getDatabaseUser());
        comboPooledDataSource.setPassword(AppConfig.getClearDatabasePassword());
        comboPooledDataSource.setMinPoolSize(2);
        comboPooledDataSource.setAcquireIncrement(2);
        comboPooledDataSource.setMaxPoolSize(10);
        comboPooledDataSource.setMaxIdleTime(1);
        comboPooledDataSource.setMaxStatements(180); //set PreaperedStatementPooling
        // comboPooledDataSource.setMaxConnectionAge(5);
        comboPooledDataSource.setAutomaticTestTable("c3p0testing");
        // Configure timeouts for better reliability
        comboPooledDataSource.setCheckoutTimeout(5000); // 5 seconds
        comboPooledDataSource.setAcquireRetryAttempts(1);
        comboPooledDataSource.setAcquireRetryDelay(1000);
    }

    private ConnectionPoolFactory() {

    }

    public static int getMaxConnections() {
        try {
            return comboPooledDataSource.getNumConnectionsDefaultUser();
        } catch (SQLException ex) {
            return 0;
        }
    }

    public static int getBusyConnections() {
        try {
            return comboPooledDataSource.getNumBusyConnectionsDefaultUser();
        } catch (SQLException ex) {
            return 0;
        }
    }

    public static int getIdleConnections() {
        try {
            return comboPooledDataSource.getNumIdleConnectionsDefaultUser();
        } catch (SQLException ex) {
            return 0;
        }
    }

    public void updateMaxConnections(int max) {
        comboPooledDataSource.setMaxPoolSize(max);
    }

    public static Connection getConnection() {
        try {
            return comboPooledDataSource.getConnection();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to get database connection from pool", ex);
        }
    }

}
