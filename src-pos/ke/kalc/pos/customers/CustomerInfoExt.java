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
