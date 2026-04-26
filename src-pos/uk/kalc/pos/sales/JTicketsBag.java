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


package uk.kalc.pos.sales;

import javax.swing.JComponent;
import javax.swing.JPanel;
import uk.kalc.pos.forms.AppView;
import uk.kalc.pos.datalogic.DataLogicSales;
import uk.kalc.pos.sales.restaurant.JTicketsBagRestaurantMap;
import uk.kalc.pos.sales.shared.JTicketsBagShared;
import uk.kalc.pos.sales.simple.JTicketsBagSimple;

public abstract class JTicketsBag extends JPanel {

    protected AppView m_App;     
    protected DataLogicSales m_dlSales;
    protected TicketsEditor m_panelticket;    
    
    /** Creates new form JTicketsBag
     * @param oApp
     * @param panelticket */
    public JTicketsBag(AppView oApp, TicketsEditor panelticket) {        
        m_App = oApp;         
        m_panelticket = panelticket;        
        m_dlSales = (DataLogicSales) m_App.getBean("uk.kalc.pos.datalogic.DataLogicSales");
    }
    
    public abstract void activate();
    public abstract boolean deactivate();
    public abstract void deleteTicket();
    public abstract void deleteAllShared();
    public abstract void getTicketByCode(String id);
    protected abstract JComponent getBagComponent();
    protected abstract JComponent getNullComponent();
    
    public static JTicketsBag createTicketsBag(String sName, AppView app, TicketsEditor panelticket) {
        switch (sName) {
            case "standard":
                // return new JTicketsBagMulti(oApp, user, panelticket);
                return new JTicketsBagShared(app, panelticket);
            case "restaurant":
                return new JTicketsBagRestaurantMap(app, panelticket);
            default:
                // "simple"
           return new JTicketsBagSimple(app, panelticket);
        }
    }   
}
