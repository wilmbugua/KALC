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
