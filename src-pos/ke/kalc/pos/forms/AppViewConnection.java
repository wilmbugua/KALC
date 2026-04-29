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


package ke.kalc.pos.forms;

import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.Session;
import ke.kalc.data.loader.SessionFactory;

public class AppViewConnection {

    /**
     * Creates a new instance of AppViewConnection
     */
    private AppViewConnection() {
    }

    /**
     *
     * @param props
     * @return
     * @throws BasicException
     */
    public static Session createSession(AppProperties props) throws BasicException {
        return createSession();
    }

    public static Session createSession() throws BasicException {        
        return SessionFactory.getSession();
    }

    private static boolean isJavaWebStart() {

        try {
            Class.forName("javax.jnlp.ServiceManager");
            return true;
        } catch (ClassNotFoundException ue) {
            return false;
        }
    }
}
