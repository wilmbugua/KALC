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
