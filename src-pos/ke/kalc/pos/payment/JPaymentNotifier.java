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

public interface JPaymentNotifier {

    /**
     *
     * @param isPositive
     * @param isComplete
     */
    public void setStatus(boolean isPositive, boolean isComplete);
    public void setDefaultBtn(boolean value);
}
