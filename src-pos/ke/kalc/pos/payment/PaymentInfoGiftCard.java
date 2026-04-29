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

import ke.kalc.format.Formats;
import ke.kalc.pos.forms.AppLocal;

/**
 *
 *
 */
public class PaymentInfoGiftCard extends PaymentInfo {

    private double m_dPaid;
    private double m_dTotal;
    private String cardNumber;
    private String m_dCardName = null;
    private String eCardNumber = null;
    private Double eCardBalance = 0.0;

    public PaymentInfoGiftCard(double dTotal, double dPaid, String cardNumber, Double cardBalance) {
        m_dTotal = dTotal;
        m_dPaid = dPaid;
        m_dCardName = cardNumber;
        eCardNumber = cardNumber;
        eCardBalance = cardBalance;

    }

    @Override
    public Double getECardBalance() {
        return eCardBalance - m_dPaid;
    }

    public void setECardBalance(Double balance) {
        eCardBalance = balance;
    }

    public String printECardBalance() {
        return Formats.CURRENCY.formatValue(eCardBalance - m_dPaid);
    }

    @Override
    public String getECardNumber() {
        return eCardNumber;
    }

    @Override
    public PaymentInfo copyPayment() {
        return new PaymentInfoGiftCard(m_dTotal, m_dPaid, cardNumber, eCardBalance);
    }

    @Override
    public String getTransactionID() {
        return null;
    }

    @Override
    public String getName() {
        return "giftcard";
    }

    @Override
    public String getDescription() {
        return AppLocal.getIntString("paymentdescription.giftcard");
    }

    @Override
    public double getTotal() {
        return m_dTotal;
    }

    @Override
    public void addToTotal(Double balance) {
        m_dTotal = m_dTotal + balance;
    }

    @Override
    public double getPaid() {
        return m_dPaid;
    }

    @Override
    public void addToPaid(Double balance) {
        m_dPaid = m_dPaid + balance;
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
