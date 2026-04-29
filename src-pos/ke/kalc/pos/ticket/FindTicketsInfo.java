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
package ke.kalc.pos.ticket;

import java.util.Date;
import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.SerializableRead;
import ke.kalc.format.Formats;

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
