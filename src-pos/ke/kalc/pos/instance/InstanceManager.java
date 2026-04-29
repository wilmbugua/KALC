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

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class InstanceManager {
    
    private final Registry m_registry;
    private final AppMessage m_message;
    
    /** Creates a new instance of InstanceManager
     * @param message
     * @throws java.rmi.RemoteException
     * @throws java.rmi.AlreadyBoundException */
    public InstanceManager(AppMessage message) throws RemoteException, AlreadyBoundException {

        m_registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);

        m_message = message;

        AppMessage stub = (AppMessage) UnicastRemoteObject.exportObject(m_message, 0);
        m_registry.bind("AppMessage", stub); 

    }    
}
