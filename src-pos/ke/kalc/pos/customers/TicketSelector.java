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


package ke.kalc.pos.customers;

import java.awt.Component;
import java.awt.event.ActionListener;
import ke.kalc.basic.BasicException;

/**
 *
 *    - outline/prep for uniCenta mobile + eCommerce connector
 */
public interface TicketSelector {

    /**
     *
     * @throws BasicException
     */
    public void loadCustomers() throws BasicException;

    /**
     *
     * @param value
     */
    public void setComponentEnabled(boolean value);

    /**
     *
     * @return
     */
    public Component getComponent();

    /**
     *
     * @param l
     */
    public void addActionListener(ActionListener l);

    /**
     *
     * @param l
     */
    public void removeActionListener(ActionListener l);
}
