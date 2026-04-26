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

import java.awt.Component;
import ke.kalc.pos.customers.CustomerInfoExt;
import ke.kalc.pos.loyalty.LoyaltyCard;


public interface JPaymentInterface {
    
    /**
     *
     * @param customerext
     * @param dTotal
     * @param transactionID
     */
    public void activate(CustomerInfoExt customerext, double dTotal, String transactionID);    

    public PaymentInfo executePayment();
    public Component getComponent();
    public Component getComponent(LoyaltyCard loyaltyCard);
    
}
