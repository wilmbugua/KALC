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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.DataWrite;
import ke.kalc.data.loader.IKeyed;
import ke.kalc.data.loader.SerializableWrite;
import ke.kalc.data.loader.SerializerRead;
import ke.kalc.pos.datalogic.DataLogicSales;
import ke.kalc.data.loader.SessionFactory;

public class TaxInfo implements Serializable, SerializableWrite, IKeyed {

    private static final long serialVersionUID = -2705212098856473043L;
    private String id;
    private String name;
    private String taxcategoryid;
    private String taxcustcategoryid;
    private String parentid;
    private double rate;
    private boolean cascade;
    private Integer order;
    private boolean hasChildTax = false;
    private DataLogicSales dlSales;

    /**
     * Creates new TaxInfo
     *
     * @param id
     * @param name
     * @param taxcategoryid
     * @param taxcustcategoryid
     * @param rate
     * @param cascade
     * @param parentid
     * @param order
     * @param hasChildTax
     */
    public TaxInfo(String id, String name, String taxcategoryid, String taxcustcategoryid, String parentid, double rate, boolean cascade, Integer order, Boolean hasChildTax) {
        this.id = id;
        this.name = name;
        this.taxcategoryid = taxcategoryid;
        this.taxcustcategoryid = taxcustcategoryid;
        this.parentid = parentid;
        this.rate = rate;
        this.cascade = cascade;
        this.order = order;
        this.hasChildTax = hasChildTax;

    }

    public List getChildren(String parentid) {
        if (dlSales == null) {
            dlSales = new DataLogicSales();
            dlSales.init(SessionFactory.getSession());
        }
        try {
            return dlSales.getTaxChildrenList(parentid);
        } catch (BasicException ex) {
            Logger.getLogger(TaxInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Object getKey() {
        return id;
    }

    public void setID(String value) {
        id = value;
    }

    public String getId() {
        return id;
    }

    public Boolean hasChildTax() {
        return hasChildTax;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public String getTaxCategoryID() {
        return taxcategoryid;
    }

    public void setTaxCategoryID(String value) {
        taxcategoryid = value;
    }

    public String getTaxCustCategoryID() {
        return taxcustcategoryid;
    }

    public void setTaxCustCategoryID(String value) {
        taxcustcategoryid = value;
    }

    public String getParentID() {
        return parentid;
    }

    public void setParentID(String value) {
        parentid = value;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double value) {
        rate = value;
    }

    public boolean isCascade() {
        return cascade;
    }

    public void setCascade(boolean value) {
        cascade = value;
    }

    public Integer getOrder() {
        return order;
    }

    public Integer getApplicationOrder() {
        return order == null ? Integer.MAX_VALUE : order.intValue();
    }

    public void setOrder(Integer value) {
        order = value;
    }

    public static double ROUNDUP(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double ROUNDDOWN(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_DOWN);
        return bd.doubleValue();
    }

    public static double ROUNDEVEN(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }

    @Override
    public String toString() {
        return name;
    }

    public static SerializerRead getSerializerRead() {
        return (DataRead dr) -> new TaxInfo(
                dr.getString(1),
                dr.getString(2),
                dr.getString(3),
                dr.getString(4),
                dr.getString(5),
                dr.getDouble(6),
                dr.getBoolean(7),
                dr.getInt(8),
                dr.getBoolean(9)
        );

    }

    @Override
    public void writeValues(DataWrite dp) throws BasicException {
        dp.setString(1, id);
        dp.setString(2, name);
        dp.setString(3, taxcategoryid);
        dp.setString(4, taxcustcategoryid);
        dp.setString(5, parentid);
        dp.setDouble(6, rate);
        dp.setBoolean(7, cascade);
        dp.setInt(8, order);
        dp.setBoolean(9, hasChildTax);
    }

}
