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
 