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


package ke.kalc.data.user;

import java.awt.Component;

public interface EditorRecord extends EditorCreator {
       
    public void writeValueEOF();

    public void writeValueInsert(); 

    public void writeValueEdit(Object value); 

    public void writeValueDelete(Object value); 
    
    public void refresh();
    
    public Component getComponent(); 

    public void refreshGuid(String siteGuid);
}
