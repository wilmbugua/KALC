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


package ke.kalc.pos.catalog;

import java.awt.Component;
import java.awt.event.ActionListener;
import ke.kalc.basic.BasicException;


public interface CatalogSelector {
    

    public void loadCatalog(String siteGuid) throws BasicException;

    public void showCatalogPanel(String id);

    public void setComponentEnabled(boolean value);

    public Component getComponent();
    
    public void addActionListener(ActionListener l);  

    public void removeActionListener(ActionListener l);    
    

}
