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
package uk.kalc.pos.ticket;

import java.util.Date;
import uk.kalc.basic.BasicException;
import uk.kalc.data.loader.DataRead;
import uk.kalc.data.loader.SerializableRead;
import uk.kalc.format.Formats;

/**
 *
 * @author Mikel irurita
 */
public class FindTicketsInfo implements SerializableRead {

    private int ticketid;
    private int tickettype;
    private Date date;
    private String name;
    private String customer;
    private double total;

    /**
     * Creates new ProductInfo
     */
    public FindTicketsInfo() {

    }

    /**
     *
     * @param dr
     * @throws BasicException
     */
    @Override
    public void readValues(DataRead dr) throws BasicException {

        ticketid = dr.getInt(1);
        tickettype = dr.getInt(2);
        date = dr.getTimestamp(3);
        name = dr.getString(4);
        customer = dr.getString(5);
        total = (dr.getObject(6) == null) ? 0.0 : dr.getDouble(6);
    }

    @Override
    public String toString() {
        String sCustomer = (customer == null) ? "" : customer;

        String sHtml = "<tr><td width=\"50\"><font size=+1> "
                + "[" + ticketid + "]" + "</font size=+1></td>"
                + "<td width=\"450\">" 
                + Formats.TIMESTAMP.formatValue(date)
                + "&nbsp;&nbsp;&nbsp;&nbsp;"
                + sCustomer
                + "<br>  "                
                + Formats.CURRENCY.formatValue(total)
                + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                + Formats.STRING.formatValue(name)
                + "</td>";

        return sHtml;
    }

    /**
     *
     * @return
     */
    public int getTicketId() {
        return this.ticketid;
    }

    /**
     *
     * @return
     */
    public int getTicketType() {
        return this.tickettype;
    }

}
