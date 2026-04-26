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


package ke.kalc.pos.instance;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class InstanceQuery {
    
    private final AppMessage m_appstub;
    
    /** Creates a new instance of InstanceQuery
     * @throws java.rmi.RemoteException
     * @throws java.rmi.NotBoundException */
    public InstanceQuery() throws RemoteException, NotBoundException {
        
        Registry registry = LocateRegistry.getRegistry();           
        m_appstub = (AppMessage) registry.lookup("AppMessage");
    }
    
    /**
     *
     * @return
     */
    public AppMessage getAppMessage() {
        return m_appstub;
    }
}
