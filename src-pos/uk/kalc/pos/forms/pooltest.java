/*
**    KALC POS  - Open Source Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous Openbravo POS related works   
**
**    https://www.KALC.co.uk
**   
**    KALC POS is free software: you can redistribute it and/or modify
**    it under the terms of the GNU General Public License as published by
**    the Free Software Foundation, either version 3 of the License, or
**    (at your option) any later version.
**
**    KALC POS is distributed in the hope that it will be useful,
**    but WITHOUT ANY WARRANTY; without even the implied warranty of
**    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
**    GNU General Public License for more details.
**
**    You should have received a copy of the GNU General Public License
**    along with KALC POS.  If not, see <http://www.gnu.org/licenses/>
**
*/


package uk.kalc.pos.forms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.kalc.connectionpool.ConnectionPoolFactory;

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
