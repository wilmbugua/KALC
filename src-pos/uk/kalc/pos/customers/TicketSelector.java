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


package uk.kalc.pos.customers;

import java.awt.Component;
import java.awt.event.ActionListener;
import uk.kalc.basic.BasicException;

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
