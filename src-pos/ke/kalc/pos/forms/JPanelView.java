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

import javax.swing.JComponent;
import ke.kalc.basic.BasicException;

public interface JPanelView {

    public abstract String getTitle();
    public abstract void activate() throws BasicException;
    public abstract boolean deactivate();
    public abstract JComponent getComponent();
}
