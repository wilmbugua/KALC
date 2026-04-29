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


package ke.kalc.pos.inventory;

import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.IKeyed;
import ke.kalc.data.loader.SerializableRead;


public class ProductListItem implements SerializableRead, IKeyed {
    
    private static final long serialVersionUID = 9032683595445L;
    private String m_sName;
    private String m_sProduct;
    private String m_sReference;
    
    public ProductListItem() {
        m_sName = null;
        m_sProduct = null;
        m_sReference = null;
    }
    
    public Object getKey() {
        return m_sProduct;
    }

    public void readValues(DataRead dr) throws BasicException {
        m_sProduct = dr.getString(1);
        m_sReference = dr.getString(2);
        m_sName = dr.getString(3);
    }

    public void setID(String sID) {
        m_sProduct = sID;
    }
    
    public String getID() {
        return m_sProduct;
    }

    public String getName() {
        return m_sName;
    }
    
    public void setName(String sName) {
        m_sName = sName;
    }  

    public String getReference() {
        return m_sReference;
    }
    
    public void setReference(String sReference) {
        m_sReference = sReference;
    }  

    
    /**
     *
     * @return
     */
    public String getProduct() {
        return m_sProduct;
    }
    
    /**
     *
     * @param sName
     */
    public void setProduct(String sName) {
        m_sProduct = sName;
    }  
    
    public String toString(){
        return m_sReference + "-" + m_sName;
    }    
}
