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

import javax.swing.JComponent;
import ke.kalc.basic.BasicException;

public interface JPanelView {

    public abstract String getTitle();
    public abstract void activate() throws BasicException;
    public abstract boolean deactivate();
    public abstract JComponent getComponent();
}
