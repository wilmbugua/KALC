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

import ke.kalc.globals.SystemProperty;
import ke.kalc.pos.forms.AppConfig;

/**
 *
 *   
 */
public class PaymentGatewayFac {
    
    /** Creates a new instance of PaymentGatewayFac */
    private PaymentGatewayFac() {
    }
    
    /**
     *
     * @param props
     * @return
     */
    public static PaymentGateway getPaymentGateway() {
        
        String sReader = SystemProperty.GATEWAY;
        switch (sReader) {
            case "external":
                return new PaymentGatewayExt();
            default:
                return null;
        }
    }      
}
