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


package ke.kalc.pos.instance;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AppMessage extends Remote {
    
    /**
     *
     * @throws RemoteException
     */
    public void restoreWindow() throws RemoteException;    
}
