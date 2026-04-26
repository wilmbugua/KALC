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

import ke.kalc.globals.SystemProperty;
import ke.kalc.format.Formats;
import ke.kalc.pos.forms.AppLocal;

/**
 *
 *
 */
public class PaymentInfoLoyalty extends PaymentInfo {

    private double m_dPaid;
    private double m_dTotal;
    private String m_dCardName = null;
    private Integer pointsBurned = 0;
    private String eCardNumber = null;

    public PaymentInfoLoyalty(double dTotal, double dPaid) {
        m_dTotal = dTotal;
        m_dPaid = dPaid;
        pointsBurned = (int) (m_dPaid / SystemProperty.REDEEMVALUE) * SystemProperty.VOUCHERPOINTS;

    }

    @Override
    public PaymentInfo copyPayment() {
        return new PaymentInfoLoyalty(m_dTotal, m_dPaid);
    }

    @Override
    public String getTransactionID() {
        return null;
    }

    @Override
    public String getName() {
        return "loyalty";
    }

    @Override
    public String getDescription() {
        return AppLocal.getIntString("paymentdescription.loyalty");
    }

    @Override
    public double getTotal() {
        return m_dTotal;
    }

    @Override
    public double getPaid() {
        return m_dPaid;
    }

    @Override
    public double getTendered() {
        return m_dPaid;
    }

    @Override
    public double getChange() {
        return m_dPaid - m_dTotal;
    }

    @Override
    public int getBurnPoints() {
        return pointsBurned;
    }

    @Override
    public String getCardName() {
        return m_dCardName;
    }

    @Override
    public Boolean isCardPayment() {
        return false;
    }

    public String printPaid() {
        return Formats.CURRENCY.formatValue(Double.valueOf(m_dPaid));
    }

    public String printChange() {
        return Formats.CURRENCY.formatValue(Double.valueOf(m_dPaid - m_dTotal));
    }

}
