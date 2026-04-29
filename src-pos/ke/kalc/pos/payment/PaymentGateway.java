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

/**
 *
 *   
 */
public interface PaymentGateway {

    /**
     *
     * @param payinfo
     */
    public void execute(PaymentInfoMagcard payinfo);
}
