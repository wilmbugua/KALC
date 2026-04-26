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
public class PaymentGatewayExt implements PaymentGateway {
    
    /** Creates a new instance of PaymentGatewayExt */
    public PaymentGatewayExt() {
    }
  
    /**
     *
     * @param payinfo
     */
    @Override
    public void execute(PaymentInfoMagcard payinfo) {
        payinfo.paymentOK("OK", payinfo.getTransactionID() , "External");
    }
}
