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

public class PaymentPanelFac {
    
    /** Creates a new instance of PaymentPanelFac */
    private PaymentPanelFac() {
    }
    
    /**
     *
     * @param sReader
     * @param notifier
     * @return
     */
    public static PaymentPanel getPaymentPanel(String sReader, JPaymentNotifier notifier) {
        switch (sReader) {
            default:
                // "Not defined
           return new PaymentPanelBasic(notifier);
        }
    }      
}
