/*
**    KALC POS  - Open Source Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous Openbravo POS related works   
**
**    https://www.KALC.co.uk
**   
**    KALC POS is free software: you can redistribute it and/or modify
**    it under the terms of the GNU General Public License as published by
**    the Free Software Foundation, either version 3 of the License, or
**    (at your option) any later version.
**
**    KALC POS is distributed in the hope that it will be useful,
**    but WITHOUT ANY WARRANTY; without even the implied warranty of
**    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
**    GNU General Public License for more details.
**
**    You should have received a copy of the GNU General Public License
**    along with KALC POS.  If not, see <http://www.gnu.org/licenses/>
**
 */
package uk.kalc.pos.payment;

import uk.kalc.format.Formats;
import uk.kalc.pos.forms.AppLocal;

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
