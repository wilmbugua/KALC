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
package ke.kalc.data.loader;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import ke.kalc.connectionpool.ConnectionPoolFactory;

public class SessionFactory {

    private static Session session;

    private SessionFactory() {
    }

    public static Session getSession() {
        if (session != null) {
            return session;
        }
        try {
            session = new Session(ConnectionPoolFactory.getConnection());
            return session;
        } catch (SQLException ex) {
            Logger.getLogger(SessionFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    }
