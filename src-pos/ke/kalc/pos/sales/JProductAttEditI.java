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


package ke.kalc.pos.sales;

import java.awt.Component;


public interface JProductAttEditI {

    /**
     *
     * @return
     */
    public String getAttribute();

    /**
     *
     * @return
     */
    public String getValue();

    /**
     *
     * @return
     */
    public Component getComponent();

    /**
     *
     */
    public void assignSelection();
}
