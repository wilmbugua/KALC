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

import javax.swing.JComponent;

/**
 *
 *   
 */
public interface PaymentPanel {
    
    /**
     *
     * @param sTransaction
     * @param dTotal
     */
    public void activate(String sTransaction, double dTotal);

    /**
     *
     * @return
     */
    public JComponent getComponent();

    /**
     *
     * @return
     */
    public PaymentInfoMagcard getPaymentInfoMagcard();
}
