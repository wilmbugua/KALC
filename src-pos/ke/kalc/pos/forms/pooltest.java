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


package ke.kalc.pos.forms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import ke.kalc.connectionpool.ConnectionPoolFactory;

/**
 *
 * @author John
 */
public class pooltest {

    public static void main(final String args[]) throws SQLException {

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPoolFactory.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from products");
            while (resultSet.next()) {
                System.out.println("empId:" + resultSet.getString(1));
                System.out.println("empName:" + resultSet.getString(2));
                System.out.println("dob:" + resultSet.getString(3));
                System.out.println("designation:" + resultSet.getString(5));
            }
        } catch (SQLException ex) {
            Logger.getLogger(pooltest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            resultSet.close();
            statement.close();
            connection.close();
        }

        try {
            connection = ConnectionPoolFactory.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from products");
            while (resultSet.next()) {
                System.out.println("empId:" + resultSet.getString(1));
                System.out.println("empName:" + resultSet.getString(2));
                System.out.println("dob:" + resultSet.getString(3));
                System.out.println("designation:" + resultSet.getString(5));
            }
        } catch (SQLException ex) {
            Logger.getLogger(pooltest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            resultSet.close();
            statement.close();
            connection.close();
        }

        try {
            connection = ConnectionPoolFactory.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from products");
            while (resultSet.next()) {
                System.out.println("empId:" + resultSet.getString(1));
                System.out.println("empName:" + resultSet.getString(2));
                System.out.println("dob:" + resultSet.getString(3));
                System.out.println("designation:" + resultSet.getString(5));
            }
        } catch (SQLException ex) {
            Logger.getLogger(pooltest.class.getName()).log(Level.SEVERE, null, ex);
        } finally 
        {
            resultSet.close();
            statement.close();
            connection.close();
        }

        try {
            connection = ConnectionPoolFactory.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from products");
            while (resultSet.next()) {
                System.out.println("empId:" + resultSet.getString(1));
                System.out.println("empName:" + resultSet.getString(2));
                System.out.println("dob:" + resultSet.getString(3));
                System.out.println("designation:" + resultSet.getString(5));
            }
        } catch (SQLException ex) {
            Logger.getLogger(pooltest.class.getName()).log(Level.SEVERE, null, ex);
        } finally
        {
            resultSet.close();
            statement.close();
            connection.close();
        }

        try {
            connection = ConnectionPoolFactory.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from products");
            while (resultSet.next()) {
                System.out.println("empId:" + resultSet.getString(1));
                System.out.println("empName:" + resultSet.getString(2));
                System.out.println("dob:" + resultSet.getString(3));
                System.out.println("designation:" + resultSet.getString(5));
            }
        } catch (SQLException ex) {
            Logger.getLogger(pooltest.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally {
            resultSet.close();
            statement.close();
            connection.close();
        }

        try {
            TimeUnit.SECONDS.sleep(25);
        } catch (InterruptedException ex) {
            Logger.getLogger(pooltest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
