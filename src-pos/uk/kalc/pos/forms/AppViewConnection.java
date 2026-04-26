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

import uk.kalc.basic.BasicException;
import uk.kalc.data.loader.Session;
import uk.kalc.data.loader.SessionFactory;

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
