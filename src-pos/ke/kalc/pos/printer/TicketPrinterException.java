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


package ke.kalc.pos.printer;

/**
 *
 *   
 */
public class TicketPrinterException extends java.lang.Exception {

    /**
     *
     */
    public TicketPrinterException() {
    }

    /**
     *
     * @param msg
     */
    public TicketPrinterException(String msg) {
        super(msg);
    }

    /**
     *
     * @param msg
     * @param cause
     */
    public TicketPrinterException(String msg, Throwable cause) {
        super(msg, cause);
    }
}


