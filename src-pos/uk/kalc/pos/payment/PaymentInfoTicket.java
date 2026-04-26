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

import uk.kalc.basic.BasicException;
import uk.kalc.data.loader.DataRead;
import uk.kalc.data.loader.SerializableRead;
import uk.kalc.format.Formats;
import uk.kalc.pos.forms.LocalResource;

public class PaymentInfoTicket extends PaymentInfo implements SerializableRead {

    private static final long serialVersionUID = 8865238639097L;
    private double m_dTicket;
    private String m_sName;
    private String m_transactionID;
    private double m_dTendered;
    private double m_dChange;
    private String m_dCardName = null;

    private String eCardNumber = null;
    private Double eCardBalance = 0.0;
    private Integer pointsEarned = 0;
    private Integer pointsBurned = 0;
    private Integer eCardPointsbalance = 0;

    public PaymentInfoTicket(double dTicket, String sName) {
        m_sName = sName;
        m_dTicket = dTicket;
    }

    public PaymentInfoTicket(double dTicket, String sName, String transactionID) {
        m_sName = sName;
        m_dTicket = dTicket;
        m_transactionID = transactionID;
    }

    public PaymentInfoTicket() {
        m_sName = null;
        m_dTicket = 0.0;
        m_transactionID = null;
        m_dTendered = 0.00;
    }

    @Override
    public void readValues(DataRead dr) throws BasicException {
        m_sName = dr.getString(1);
        m_dTicket = dr.getDouble(2);
        m_transactionID = dr.getString(3);
        if (dr.getDouble(4) != null) {
            m_dTendered = dr.getDouble(4);
        }
        m_dCardName = dr.getString(5);
        eCardNumber = dr.getString(6);
        eCardBalance = dr.getDouble(7);
        m_dChange = m_dTendered - m_dTicket;
    }

    @Override
    public PaymentInfo copyPayment() {
        return new PaymentInfoTicket(m_dTicket, m_sName);
    }

    @Override
    public String getName() {
        return m_sName;
    }

    @Override
    public String getDescription() {
        return LocalResource.getString("paymentdescription." + m_sName);
    }

    @Override
    public double getTotal() {
        return m_dTicket;
    }

    @Override
    public String getTransactionID() {
        return m_transactionID;
    }

    @Override
    public double getPaid() {
        if (m_dTendered != 0) {
            return m_dTendered;
        } else {
            return m_dTicket;
        }
    }

    @Override
    public double getChange() {
        return m_dTendered - m_dTicket;
    }

    @Override
    public double getTendered() {
        return (0.00);
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
        return Formats.CURRENCY.formatValue(getPaid());
    }

    public String printPaperTotal() {
        return Formats.CURRENCY.formatValue(-m_dTicket);
    }

    public String printChange() {
        return Formats.CURRENCY.formatValue(m_dTendered - m_dTicket);
    }

    public String printTendered() {
        return Formats.CURRENCY.formatValue(m_dTendered);
    }

    public String getECardNumber() {
        return eCardNumber;
    }

    public Double getECardBalance() {
        return eCardBalance;
    }

    public String printECardBalance() {
        return Formats.CURRENCY.formatValue(eCardBalance);
    }

    public Integer getECardPointsBalance() {
        return eCardPointsbalance;
    }

    public Integer getPointsEarned() {
        return pointsEarned;
    }

    public Integer getPointsBurned() {
        return pointsBurned;
    }

}
