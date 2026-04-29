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
