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


package uk.kalc.pos.inventory;

import uk.kalc.basic.BasicException;
import uk.kalc.data.loader.DataRead;
import uk.kalc.data.loader.IKeyed;
import uk.kalc.data.loader.SerializableRead;

public class ProductListInfo implements SerializableRead, IKeyed {
    
    private static final long serialVersionUID = 9032683595244L;
    private String m_sName;
    
    /** Creates a new instance of LocationInfo */
    public ProductListInfo() {
        m_sName = null;
    }
    
    /** Creates a new instance of LocationInfo */
    public ProductListInfo( String name ) {
        m_sName = name;
    }

    /**
     *
     * @return
     */
    public Object getKey() {
        return m_sName;
    }

    /**
     *
     * @param dr
     * @throws BasicException
     */
    public void readValues(DataRead dr) throws BasicException {
        m_sName = dr.getString(1);
    }

    /**
     *
     * @param sID
     */
    public void setID(String sID) {
        m_sName = sID;
    }
    
    /**
     *
     * @return
     */
    public String getID() {
        return m_sName;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return m_sName;
    }
    
    /**
     *
     * @param sName
     */
    public void setName(String sName) {
        m_sName = sName;
    }  

    public String toString(){
        return m_sName;
    }    
}
