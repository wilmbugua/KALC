/*
**    KALC POS  - Professional Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous Openbravo POS related works   
**
**    https://www.KALC.co.uk
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
            System.err.println("num_connections: " + comboPooledDataSource.getNumConnectionsDefaultUser());
            System.err.println("num_busy_connections: " + comboPooledDataSource.getNumBusyConnectionsDefaultUser());
            System.err.println("num_idle_connections: " + comboPooledDataSource.getNumIdleConnectionsDefaultUser());
            System.err.println();
            return comboPooledDataSource.getConnection();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return null;
    }

}
