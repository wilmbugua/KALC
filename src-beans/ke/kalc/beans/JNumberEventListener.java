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


package ke.kalc.beans;

import java.util.EventListener;

public interface JNumberEventListener extends EventListener {

    /**
     *
     * @param e
     */
    public void keyPerformed(JNumberEvent e);
}
