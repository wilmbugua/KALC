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

import ke.kalc.pos.forms.AppLocal;

/**
 *
 *
 */
public class PaymentInfoFree extends PaymentInfo {

    private double m_dTotal;
    private double m_dTendered;
    private String m_dCardName = null;
    private int pointsBurned = 0;

    /**
     * Creates a new instance of PaymentInfoFree
     *
     * @param dTotal
     */
    public PaymentInfoFree(double dTotal) {
        m_dTotal = dTotal;
    }

    /**
     *
     * @return
     */
    @Override
    public PaymentInfo copyPayment() {
        return new PaymentInfoFree(m_dTotal);
    }

    @Override
    public String getTransactionID() {
        return null;
    }

    @Override
    public String getName() {
        return "free";
    }

    @Override
    public String getDescription() {
        return AppLocal.getIntString("paymentdescription.free");
    }

    @Override
    public double getTotal() {
        return m_dTotal;
    }

    @Override
    public double getPaid() {
        return (0.0);
    }

    @Override
    public double getChange() {
        return (0.00);
    }

    @Override
    public double getTendered() {
        return m_dTendered;
    }

    @Override
    public String getCardName() {
        return m_dCardName;
    }
    @Override
    public Boolean isCardPayment() {
        return false;
    }
    
}
