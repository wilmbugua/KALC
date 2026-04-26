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


package ke.kalc.pos.inventory;

import ke.kalc.format.Formats;
import ke.kalc.pos.ticket.ProductInfoExt;
import ke.kalc.pos.util.StringUtils;


public class InventoryLine {
    
    private double m_dMultiply;    
    private double m_dPrice;
    
    private String m_sProdID;
    private String m_sProdName;

    private String attsetid;
    private String attsetinstid;
    private String attsetinstdesc;
 
    /** Creates a new instance of InventoryLine
     * @param oProduct */
    public InventoryLine(ProductInfoExt oProduct) {
        m_sProdID = oProduct.getID();
        m_sProdName = oProduct.getName();
        m_dMultiply = 1.0;
        m_dPrice = oProduct.getPriceBuy();
        attsetid = oProduct.getAttributeSetID();
        attsetinstid = null;
        attsetinstdesc = null;
    }
    
    /**
     *
     * @param oProduct
     * @param dpor
     * @param dprice
     */
    public InventoryLine(ProductInfoExt oProduct, double dpor, double dprice) {
        m_sProdID = oProduct.getID();
        m_sProdName = oProduct.getName();
        m_dMultiply = dpor;
        m_dPrice = dprice;
        attsetid = oProduct.getAttributeSetID();
        attsetinstid = null;
        attsetinstdesc = null;
    }
    
    /**
     *
     * @return
     */
    public String getProductID() {
        return m_sProdID;
    }

    /**
     *
     * @return
     */
    public String getProductName() {
        return m_sProdName;
    } 

    /**
     *
     * @param sValue
     */
    public void setProductName(String sValue) {
        if (m_sProdID == null) {
            m_sProdName = sValue;
        }
    }

    /**
     *
     * @return
     */
    public double getMultiply() {
        return m_dMultiply;
    }
    
    /**
     *
     * @param dValue
     */
    public void setMultiply(double dValue) {
        m_dMultiply = dValue;
    }
    
    /**
     *
     * @return
     */
    public double getPrice() {
        return m_dPrice;
    }
    
    /**
     *
     * @param dValue
     */
    public void setPrice(double dValue) {
        m_dPrice = dValue;
    }

    /**
     *
     * @return
     */
    public double getSubValue() {
        return m_dMultiply * m_dPrice;
    }
    
    /**
     *
     * @return
     */
    public String getProductAttSetInstId() {
        return attsetinstid;
    }

    /**
     *
     * @param value
     */
    public void setProductAttSetInstId(String value) {
        attsetinstid = value;
    }

    /**
     *
     * @return
     */
    public String getProductAttSetId() {
        return attsetid;
    }

    /**
     *
     * @return
     */
    public String getProductAttSetInstDesc() {
        return attsetinstdesc;
    }

    /**
     *
     * @param value
     */
    public void setProductAttSetInstDesc(String value) {
        attsetinstdesc = value;
    }
    
    /**
     *
     * @return
     */
    public String printName() {
        return StringUtils.encodeXML(m_sProdName);
    }
    
    /**
     *
     * @return
     */
    public String printPrice() {
        if (m_dMultiply == 1.0) {
            return "";
        } else {
            return Formats.CURRENCY.formatValue(getPrice());
        }
    }
    
    /**
     *
     * @return
     */
    public String printMultiply() {
        return Formats.DOUBLE.formatValue(m_dMultiply);
    }
    
    /**
     *
     * @return
     */
    public String printSubValue() {
        return Formats.CURRENCY.formatValue(getSubValue());
    }    
}
