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


package ke.kalc.pos.ticket;

import static java.lang.Math.PI;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.commons.math3.util.Precision;
import ke.kalc.globals.SystemProperty;

public class TicketTaxInfo {

    private final TaxInfo tax;

    private double subtotal;
    private double parenttax;
    private double taxtotal;

    public TicketTaxInfo(TaxInfo tax) {
        this.tax = tax;
        subtotal = 0.0;
        taxtotal = 0.0;
        parenttax = 0.0;
    }

    public TaxInfo getTaxInfo() {
        return tax;
    }

    public TicketTaxInfo() {
        tax = null;
        subtotal = 0.0;
        taxtotal = 0.0;
        parenttax = 0.0;
    }
   
    
    public void add(double dValue) {
        subtotal += dValue;
        if (SystemProperty.TAXINCLUDED) {
            taxtotal = subtotal - (subtotal / (1 + tax.getRate()));
        } else {
            taxtotal = subtotal * tax.getRate();
        }
    }

    public void addExclusive(double dValue) {
        parenttax += dValue;
    }

    public double getSubTotal() {
        if (SystemProperty.TAXINCLUDED) {
            return subtotal / (1 + tax.getRate());
        } else {
            return subtotal;
        }
    }

    public void setParentTaxRate(Double rate) {
        parenttax = rate;
    }

    public double getSubTotalExcluding() {
        return subtotal;
    }

    public double getSubTotalIncluding() {        
        if (tax.getParentID() != null) {
            return subtotal / (1 + parenttax);
        }
        return subtotal / (1 + tax.getRate());
    }

    public double getTax() {
        return subtotal * tax.getRate();
    }

    public double getTaxExcluding() {       
        return roundHalfEven(subtotal * tax.getRate());
    }

    public double getTaxIncluding() {
        return getSubTotalIncluding() * tax.getRate();
    }
    private Double roundHalfEven(Double value) {       
        BigDecimal bd = new BigDecimal(value).setScale(3, RoundingMode.HALF_EVEN);     
        return bd.doubleValue();
    }
}
 