/*
**    KALC Administration  - Open Source Point of Sale
**
**    This file is part of KALC Administration Version KALC V1.5.0
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
package ke.kalc.pos.repair;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import ke.kalc.basic.BasicException;
import ke.kalc.connectionpool.ConnectionPoolFactory;
import ke.kalc.data.loader.Datas;
import ke.kalc.data.loader.PreparedSentence;
import ke.kalc.data.loader.SerializerReadInteger;
import ke.kalc.data.loader.SerializerWriteBasicExt;
import ke.kalc.data.loader.Session;
import ke.kalc.data.loader.SessionFactory;
import ke.kalc.data.loader.StaticSentence;
import ke.kalc.pos.forms.LocalResource;

/**
 *
 * @author John
 */
public class DatabaseRepair {

    private static final Session s = SessionFactory.getSession();

    /**
     * Creates a new instance of DatabaseRepair this will be removed in a later
     * version only required to allow report fix
     */
    public DatabaseRepair() {
    }

    public static void repairPayments() {

        try {

            Object m_result = new StaticSentence(s,
                    "select count(*) from payments where description is null or description =''",
                    null, SerializerReadInteger.INSTANCE).find();
            if ((Integer) m_result == 0) {
                return;
            }

            new PreparedSentence(s, "drop trigger if exists update_payments;", null).exec();

            Connection connection;
            Statement statement = null; 
            ResultSet resultSet = null;
            try {
                connection = ConnectionPoolFactory.getConnection();
                statement = connection.createStatement();
                resultSet = statement.executeQuery("select * from payments");
                while (resultSet.next()) {
                    new PreparedSentence(s, "update payments set description = ? where id = ? ",
                            new SerializerWriteBasicExt(new Datas[]{
                        Datas.OBJECT, Datas.STRING,
                        Datas.OBJECT, Datas.STRING}, new int[]{0, 1})).exec(LocalResource.getString("paymentdescription." + resultSet.getString("payment")), resultSet.getString("id"));
                }
            } catch (SQLException ex) {

            } finally {
                try {
                    resultSet.close();
                    statement.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseRepair.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            String sql = " create definer = current_user "
                    + " trigger update_payments before update on payments "
                    + " for each row"
                    + " begin "
                    + " SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'UPDATE cancelled payments';"
                    + " end; ";

            new PreparedSentence(s, sql, null).exec();

        } catch (BasicException ex) {
            Logger.getLogger(DatabaseRepair.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
