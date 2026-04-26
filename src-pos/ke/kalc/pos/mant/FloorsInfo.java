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


package ke.kalc.pos.mant;


import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.IKeyed;
import ke.kalc.data.loader.SerializableRead;

public class FloorsInfo implements SerializableRead, IKeyed {

    private static final long serialVersionUID = 8906929819402L;
    private String m_sID;
    private String m_sName;
    private String m_sSiteGuid;
    private byte[] m_bImage;


    public FloorsInfo() {
        m_sID = null;
        m_sName = null;
        m_bImage = null;
        m_sSiteGuid = null;
    }

    public FloorsInfo(String id, String name){
        m_sID = id;
        m_sName = name;
    }
    
    
    public String getSiteGuid() {
        return m_sSiteGuid;
    }

    public byte[] getImage() {
        return m_bImage;
    }

    public void setImage(byte[] image) {
        this.m_bImage = image;
    }

    public void setSiteGuid(String siteGuid) {
        this.m_sSiteGuid = siteGuid;
    }

    @Override
    public Object getKey() {
        return m_sID;
    }

    @Override
    public void readValues(DataRead dr) throws BasicException {
        m_sID = dr.getString(1);
        m_sName = dr.getString(2);
        m_bImage = dr.getBytes(3);
        m_sSiteGuid = dr.getString(4);
    }

    public void setID(String sID) {
        m_sID = sID;
    }

    public String getID() {
        return m_sID;
    }

    public String getName() {
        return m_sName;
    }

    public void setName(String sName) {
        m_sName = sName;
    }

    @Override
    public String toString() {
        return m_sName;
    }
}
