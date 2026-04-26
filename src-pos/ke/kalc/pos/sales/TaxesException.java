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


package ke.kalc.pos.sales;


public class TaxesException extends Exception {

    /**
     * Creates a new instance of <code>TaxesException</code> without detail message.
     * @param t
     */
    public TaxesException(Throwable t) {
        super(t);
    }
    
    /**
     *
     * @param msg
     * @param t
     */
    public TaxesException(String msg, Throwable t) {
        super(msg, t);
    }
    /**
     * Constructs an instance of <code>TaxesException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public TaxesException(String msg) {
        super(msg);
    }
}
