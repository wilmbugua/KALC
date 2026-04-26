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
