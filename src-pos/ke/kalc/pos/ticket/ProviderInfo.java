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

/**
 *
 *   
 */
public class ProviderInfo {
    
    private int m_iProviderID;
    private String m_sName;

    /** Creates new Provider */
    public ProviderInfo() {
        m_iProviderID = 0;
        m_sName = "";
    }
    
    /**
     *
     * @return
     */
    public int getProviderID() {
        return m_iProviderID;
    }
    
    /**
     *
     * @param iProviderID
     */
    public void setProviderID(int iProviderID) {
        m_iProviderID = iProviderID;
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
