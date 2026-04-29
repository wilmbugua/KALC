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
