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

import ke.kalc.format.Formats;

public abstract class PaymentInfo {

    public abstract String getName();

    public abstract String getDescription();

    public abstract double getTotal();

    public abstract PaymentInfo copyPayment();

    public abstract String getTransactionID();

    public abstract double getPaid();

    public abstract double getChange();

    public abstract double getTendered();

    public abstract String getCardName();
    
    public abstract Boolean isCardPayment();

    public void addToPaid(Double balance) {       
    }
     
    public void addToTotal(Double balance) {       
    }

    public int getBurnPoints() {
        return 0;
    }

    public Double getECardBalance() {
        return 0.00;
    }

    public String getECardNumber() {
        return null;
    }

    public String printTotal() {
        return Formats.CURRENCY.formatValue(getTotal());
    }
}
