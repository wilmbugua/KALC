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

import ke.kalc.pos.ticket.TicketInfo;

/**
 *
 *   
 */
public interface TicketsEditor {
    
    /**
     *
     * @param oTicket
     * @param oTicketExt
     */
    public void setActiveTicket(TicketInfo oTicket, Object oTicketExt); 

    public void setTicketName(String tName);
    /**
     *
     * @return
     */
    public TicketInfo getActiveTicket(); 
    
    public void addServiceChargeLine();
}
