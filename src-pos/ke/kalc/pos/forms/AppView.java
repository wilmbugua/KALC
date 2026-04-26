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

import java.util.Date;
import ke.kalc.data.loader.Session;
import ke.kalc.pos.printer.DeviceTicket;
import ke.kalc.pos.scale.DeviceScale;
import ke.kalc.pos.scanpal2.DeviceScanner;

public interface AppView {
    
    public DeviceScale getDeviceScale();

    public DeviceTicket getDeviceTicket();

    public DeviceScanner getDeviceScanner();
      
    public Session getSession();

    public AppProperties getProperties();

    public Object getBean(String beanfactory) throws BeanFactoryException;
     
    public void setActiveCash(String value, int iSeq, Date dStart, Date dEnd);

    public String getActiveCashIndex();

    public int getActiveCashSequence();

    public Date getActiveCashDateStart();

    public Date getActiveCashDateEnd();
    
    public String getInventoryLocation();
    
    public void waitCursorBegin();

    public void waitCursorEnd();
    
    public AppUserView getAppUserView();
}

