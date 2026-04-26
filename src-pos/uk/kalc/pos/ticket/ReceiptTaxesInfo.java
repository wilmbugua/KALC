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


package uk.kalc.pos.ticket;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.kalc.format.Formats;

/**
 *
 * @author John
 */
public class ReceiptTaxesInfo {

    private String receiptTaxName;
    private Double receiptTaxNett;
    private Double receiptTaxRate;
    private Double receiptTax;
    private Double receiptSubTotal;
    private List<LineTaxRates> ltr;

    public ReceiptTaxesInfo() {

    }

    public ReceiptTaxesInfo(String taxName, Double taxNett, Double taxRate) {
        receiptTaxName = taxName;
        receiptTaxNett = taxNett;
        receiptTaxRate = taxRate;
        receiptTax = roundHalfEven(taxNett * taxRate);
    }

    public List<ReceiptTaxesInfo> getReceiptTaxLines(List<LineTaxRates> linetaxes) {

        Map<String, Double> receiptTaxLines = new HashMap<>();
        Map<String, Double> receiptTaxRates = new HashMap<>();

        linetaxes.forEach((taxes) -> {
            if (taxes.hasChildren()) {
                if (taxes.getChildtax1() > 0.00) {
                    receiptTaxLines.putIfAbsent(taxes.getChildtaxname1(), 0.00);
                    receiptTaxLines.put(taxes.getChildtaxname1(), receiptTaxLines.get(taxes.getChildtaxname1()) + taxes.getLinenett());
                    receiptTaxRates.putIfAbsent(taxes.getChildtaxname1(), taxes.getChildtaxRate1());
                }
                if (taxes.getChildtax2() > 0.00) {
                    receiptTaxLines.putIfAbsent(taxes.getChildtaxname2(), 0.00);
                    receiptTaxLines.put(taxes.getChildtaxname2(), receiptTaxLines.get(taxes.getChildtaxname2()) + taxes.getLinenett());
                    receiptTaxRates.putIfAbsent(taxes.getChildtaxname2(), taxes.getChildtaxRate2());
                }
                if (taxes.getChildtax3() > 0.00) {
                    receiptTaxLines.putIfAbsent(taxes.getChildtaxname3(), 0.00);
                    receiptTaxLines.put(taxes.getChildtaxname3(), receiptTaxLines.get(taxes.getChildtaxname3()) + taxes.getLinenett());
                    receiptTaxRates.putIfAbsent(taxes.getChildtaxname3(), taxes.getChildtaxRate3());
                }
                if (taxes.getChildtax4() > 0.00) {
                    receiptTaxLines.putIfAbsent(taxes.getChildtaxname4(), 0.00);
                    receiptTaxLines.put(taxes.getChildtaxname4(), receiptTaxLines.get(taxes.getChildtaxname4()) + taxes.getLinenett());
                    receiptTaxRates.putIfAbsent(taxes.getChildtaxname4(), taxes.getChildtaxRate4());
                }
                if (taxes.getChildtax5() > 0.00) {
                    receiptTaxLines.putIfAbsent(taxes.getChildtaxname4(), 0.00);
                    receiptTaxLines.put(taxes.getChildtaxname5(), receiptTaxLines.get(taxes.getChildtaxname5()) + taxes.getLinenett());
                    receiptTaxRates.putIfAbsent(taxes.getChildtaxname5(), taxes.getChildtaxRate5());
                }
                if (taxes.getChildtax6() > 0.00) {
                    receiptTaxLines.putIfAbsent(taxes.getChildtaxname6(), 0.00);
                    receiptTaxLines.put(taxes.getChildtaxname6(), receiptTaxLines.get(taxes.getChildtaxname6()) + taxes.getLinenett());
                    receiptTaxRates.putIfAbsent(taxes.getChildtaxname6(), taxes.getChildtaxRate6());
                }
            } else {
                receiptTaxLines.putIfAbsent(taxes.getBaseName(), 0.00);
                receiptTaxLines.put(taxes.getBaseName(), receiptTaxLines.get(taxes.getBaseName()) + taxes.getLinenett());
                receiptTaxRates.putIfAbsent(taxes.getBaseName(), taxes.getBasetax());
            }
        });

        
        
        
        
        
        
        List<ReceiptTaxesInfo> returns = new ArrayList();
        receiptTaxLines.forEach((key, value) -> {
            returns.add(new ReceiptTaxesInfo(key, value, receiptTaxRates.get(key)));
        });

        return returns;

    }

    public String getReceiptTaxName() {
        return receiptTaxName;
    }

    public Double getReceiptTaxNett() {
        return receiptTaxNett;
    }

    public Double getReceiptTaxRate() {
        return receiptTaxRate;
    }

    public Double getReceiptTax() {
        return receiptTax;
    }

    public Double getReceiptSubTotal() {
        return receiptSubTotal;
    }

    public Double subReceiptSubTotal(List<ReceiptTaxesInfo> returns, double total) {
        return returns.stream().map((taxLine) -> taxLine.receiptTax).reduce(total, (accumulator, _item) -> accumulator - _item);
    }
    
    public String printReceiptTaxNett() {        
        return Formats.CURRENCY.formatValue(getReceiptTaxNett());
    }

    public String printReceiptTax() {        
        return Formats.CURRENCY.formatValue(getReceiptTax());
    }

    public String printReceiptSubTotal(List<ReceiptTaxesInfo> returns, double total) {      
        
        
        return Formats.CURRENCY.formatValue(returns.stream().map((taxLine) -> taxLine.receiptTax).reduce(total, (accumulator, _item) -> accumulator - _item));
    }

    private Double roundHalfEven(Double value) {
        BigDecimal bd = new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }

}
