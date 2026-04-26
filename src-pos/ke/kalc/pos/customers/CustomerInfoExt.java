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


package ke.kalc.pos.customers;

import java.util.Date;
import ke.kalc.format.Formats;
import ke.kalc.pos.util.RoundUtils;

public class CustomerInfoExt extends CustomerInfo {

    protected String taxcustomerid;
  
    public CustomerInfoExt(String id) {
        super(id);
    }

    public String getTaxCustCategoryID() {
        return taxcustomerid;
    }

    public void setTaxCustomerID(String taxcustomerid) {
        this.taxcustomerid = taxcustomerid;
    }

    public String printMaxDebt() {
        return Formats.CURRENCY.formatValue(RoundUtils.getValue(getMaxDebt()));
    }

    public String printDiscount() {
        return Formats.PERCENT.formatValue(RoundUtils.getValue(getCustomerDiscount()));
    }

    public String printCurDate() {
        return Formats.DATE.formatValue(getCurDate());
    }

    public String printCurDebt() {
        return Formats.CURRENCY.formatValue(RoundUtils.getValue(getCurrentDebt()));
    }

    public void updateCurDebt(Double amount, Date d) {

        currentDebt = currentDebt == null ? amount : currentDebt + amount;
        curDate = (new Date());

        if (RoundUtils.compare(currentDebt, 0.0) > 0) {
            if (curDate == null) {
                curDate = d;
            }
        } else if (RoundUtils.compare(currentDebt, 0.0) == 0) {
            currentDebt = 0.00;
            curDate = null;
        } else {
            curDate = null;
        }
    } 

}
