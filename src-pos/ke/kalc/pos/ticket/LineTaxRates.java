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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.DataWrite;
import ke.kalc.data.loader.SerializableRead;
import ke.kalc.data.loader.SerializableWrite;
import ke.kalc.data.loader.SerializerRead;


public class LineTaxRates implements SerializableWrite, SerializableRead, Serializable {

    private String id;
    private Integer line;
    private Double basetax;
    private String basename;
    private Double linenett;
    private Boolean haschildren = false;
    private Double childtax1 = 0.0;
    private Double childtax2 = 0.0;
    private Double childtax3 = 0.0;
    private Double childtax4 = 0.0;
    private Double childtax5 = 0.0;
    private Double childtax6 = 0.0;
    private Double childtaxrate1;
    private Double childtaxrate2;
    private Double childtaxrate3;
    private Double childtaxrate4;
    private Double childtaxrate5;
    private Double childtaxrate6;
    private String childtaxname1;
    private String childtaxname2;
    private String childtaxname3;
    private String childtaxname4;
    private String childtaxname5;
    private String childtaxname6;

    public LineTaxRates(String ticket, Integer line, Double basetax, String basename, Double linenett, Boolean haschildren, Double taxchild1, Double taxchild2,
            Double taxchild3, Double taxchild4, Double taxchild5, Double taxchild6, Double taxchildrate1, Double taxchildrate2,
            Double taxchildrate3, Double taxchildrate4, Double taxchildrate5, Double taxchildrate6, String childtaxname1,
            String childtaxname2, String childtaxname3, String childtaxname4, String childtaxname5, String childtaxname6) {
        this.id = ticket;
        this.line = line;
        this.basetax = basetax;
        this.basename = basename;
        this.linenett = linenett;
        this.childtax1 = taxchild1;
        this.childtax2 = taxchild2;
        this.childtax3 = taxchild3;
        this.childtax4 = taxchild4;
        this.childtax5 = taxchild5;
        this.childtax6 = taxchild6;
        this.childtaxrate1 = taxchildrate1;
        this.childtaxrate2 = taxchildrate2;
        this.childtaxrate3 = taxchildrate3;
        this.childtaxrate4 = taxchildrate4;
        this.childtaxrate5 = taxchildrate5;
        this.childtaxrate6 = taxchildrate6;
        this.childtaxname1 = childtaxname1;
        this.childtaxname2 = childtaxname2;
        this.childtaxname3 = childtaxname3;
        this.childtaxname4 = childtaxname4;
        this.childtaxname5 = childtaxname5;
        this.childtaxname6 = childtaxname6;
        this.haschildren = getHasChildren();
    }

    public LineTaxRates(TicketLineInfo ticket, TaxInfo tax) {
        this.id = ticket.getTicket();
        this.line = ticket.getTicketLine();
        this.basetax = tax.getRate();
        if (ticket.isTaxInclusive()) {
            linenett = ticket.getLinePrice() / (1 + basetax);
        } else {
            linenett = ticket.getLinePrice();
        }
        basename = tax.getName();

        List<TaxInfo> children = tax.getChildren(tax.getId());
        int index = 6;
        Field fld[] = LineTaxRates.class.getDeclaredFields();
        for (TaxInfo tInfo : children) {
            try {
                fld[index].set(this, roundHalfEven(linenett * tInfo.getRate()));
                fld[index + 6].set(this, tInfo.getRate());
                fld[index + 12].set(this, tInfo.getName());
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(LineTaxRates.class.getName()).log(Level.SEVERE, null, ex);
            }
            index++;
        }
        this.haschildren = getHasChildren();
    }

    private Double roundHalfEven(Double value) {
        BigDecimal bd = new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }

    private Boolean getHasChildren() {
        return (childtax1 + childtax2 + childtax3 + childtax4 + childtax5 + childtax6) > 0.00;
    }

    public String getId() {
        return id;
    }

    public Integer getLine() {
        return line;
    }

    public Double getBasetax() {
        return basetax;
    }

    public String getBaseName() {
        return basename;
    }

    public Double getLinenett() {
        return linenett;
    }

    public Boolean hasChildren() {
        return haschildren;
    }

    public Double getChildtax1() {
        return childtax1;
    }

    public Double getChildtax2() {
        return childtax2;
    }

    public Double getChildtax3() {
        return childtax3;
    }

    public Double getChildtax4() {
        return childtax4;
    }

    public Double getChildtax5() {
        return childtax5;
    }

    public Double getChildtax6() {
        return childtax6;
    }

    public Double getChildtaxRate1() {
        return childtaxrate1;
    }

    public Double getChildtaxRate2() {
        return childtaxrate2;
    }

    public Double getChildtaxRate3() {
        return childtaxrate3;
    }

    public Double getChildtaxRate4() {
        return childtaxrate4;
    }

    public Double getChildtaxRate5() {
        return childtaxrate5;
    }

    public Double getChildtaxRate6() {
        return childtaxrate6;
    }

    public String getChildtaxname1() {
        return childtaxname1;
    }

    public String getChildtaxname2() {
        return childtaxname2;
    }

    public String getChildtaxname3() {
        return childtaxname3;
    }

    public String getChildtaxname4() {
        return childtaxname4;
    }

    public String getChildtaxname5() {
        return childtaxname5;
    }

    public String getChildtaxname6() {
        return childtaxname6;
    }

    @Override
    public void writeValues(DataWrite dp) throws BasicException {
        dp.setString(1, id);
        dp.setInt(2, line);
        dp.setDouble(3, basetax);
        dp.setString(4, basename);
        dp.setDouble(5, linenett);
        dp.setBoolean(6, haschildren);
        dp.setDouble(7, childtax1);
        dp.setDouble(8, childtax2);
        dp.setDouble(9, childtax3);
        dp.setDouble(10, childtax4);
        dp.setDouble(11, childtax5);
        dp.setDouble(12, childtax6);
        dp.setDouble(13, childtaxrate1);
        dp.setDouble(14, childtaxrate2);
        dp.setDouble(15, childtaxrate3);
        dp.setDouble(16, childtaxrate4);
        dp.setDouble(17, childtaxrate5);
        dp.setDouble(18, childtaxrate6);
        dp.setString(19, childtaxname1);
        dp.setString(20, childtaxname2);
        dp.setString(21, childtaxname3);
        dp.setString(22, childtaxname4);
        dp.setString(23, childtaxname5);
        dp.setString(24, childtaxname6);
    }

    public static SerializerRead getSerializerRead() {
        return (DataRead dr) -> new LineTaxRates(
                dr.getString(1),
                dr.getInt(2),
                dr.getDouble(3),
                dr.getString(4),
                dr.getDouble(5),
                dr.getBoolean(6),
                dr.getDouble(7),
                dr.getDouble(8),
                dr.getDouble(9),
                dr.getDouble(10),
                dr.getDouble(11),
                dr.getDouble(12),
                dr.getDouble(13),
                dr.getDouble(14),
                dr.getDouble(15),
                dr.getDouble(16),
                dr.getDouble(17),
                dr.getDouble(18),
                dr.getString(19),
                dr.getString(20),
                dr.getString(21),
                dr.getString(22),
                dr.getString(23),
                dr.getString(24)
        );
    }

    @Override
    public void readValues(DataRead dr) throws BasicException {
        id = dr.getString(1);
        line = dr.getInt(2);
        basetax = dr.getDouble(3);
        basename = dr.getString(4);
        linenett = dr.getDouble(5);
        haschildren = dr.getBoolean(6);
        childtax1 = dr.getDouble(7);
        childtax1 = dr.getDouble(8);
        childtax1 = dr.getDouble(9);
        childtax1 = dr.getDouble(10);
        childtax1 = dr.getDouble(11);
        childtax1 = dr.getDouble(12);
        childtaxrate1 = dr.getDouble(13);
        childtaxrate1 = dr.getDouble(14);
        childtaxrate1 = dr.getDouble(15);
        childtaxrate1 = dr.getDouble(16);
        childtaxrate1 = dr.getDouble(17);
        childtaxrate1 = dr.getDouble(18);
        childtaxname1 = dr.getString(19);
        childtaxname2 = dr.getString(20);
        childtaxname3 = dr.getString(21);
        childtaxname4 = dr.getString(22);
        childtaxname5 = dr.getString(23);
        childtaxname6 = dr.getString(24);

    }

}
