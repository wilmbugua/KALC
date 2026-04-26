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


package ke.kalc.pos.payment;

/**
 *
 *   
 */
public class PaymentException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>PaymentException</code> without detail message.
     */
    public PaymentException() {
    }
   
    
    /**
     * Constructs an instance of <code>PaymentException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public PaymentException(String msg) {
        super(msg);
    }
}
