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


package ke.kalc.data.loader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import ke.kalc.commons.dbmanager.DbUser;
import ke.kalc.pos.forms.AppConfig;
import ke.kalc.pos.forms.DriverWrapper;

/**
 *
 * @author John
 */
public class ConnectionFactory {

    private static ConnectionFactory INSTANCE = new ConnectionFactory();
    private static Connection connection;

    private ConnectionFactory() {
    }

    public static ConnectionFactory getInstance() {
        if (INSTANCE == null) {
            synchronized (ConnectionFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ConnectionFactory();
                }
            }
        }
        return INSTANCE;
    }

    public Connection getConnection() {
        if (connection != null) {
            return connection;
        }
        try {
            ClassLoader cloader = new URLClassLoader(new URL[]{new File(AppConfig.getDatabaseLibraryPath()).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(AppConfig.getDatabaseProviderClass(), true, cloader).getDeclaredConstructor().newInstance()));
            connection = (Connection) DriverManager.getConnection(AppConfig.getDatabaseURL(), AppConfig.getDatabaseUser(), AppConfig.getClearDatabasePassword());
            return connection;
        } catch (SQLException ex) {
        } catch (SecurityException | IllegalArgumentException | ClassNotFoundException | MalformedURLException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            Logger.getLogger(ConnectionFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Connection getConnection(DbUser dbUser) {
        if (connection != null) {
            return connection;
        }
        try {
            ClassLoader cloader = new URLClassLoader(new URL[]{new File(dbUser.getDbLibrary()).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(dbUser.getDbClass(), true, cloader).getDeclaredConstructor().newInstance()));
            connection = (Connection) DriverManager.getConnection(dbUser.getURL(), dbUser.getUserName(), dbUser.getUserPassword());
            return connection;
        } catch (SQLException ex) {
//            System.out.println("Unable to connect to database. Bad connection details");
//            System.out.println(ex.getSQLState());
//            Logger.getLogger(ConnectionFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException | IllegalArgumentException | ClassNotFoundException | MalformedURLException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            Logger.getLogger(ConnectionFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
