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


package ke.kalc.pos.epm;

import ke.kalc.data.loader.IKeyed;

public class BreaksInfo implements IKeyed {

    private static final long serialVersionUID = 8936482715929L;
    private String m_sID;
    private String m_sName;
    private String m_siteguid;

    public String getSiteguid() {
        return m_siteguid;
    }

    public void setSiteguid(String siteguid) {
        this.m_siteguid = m_siteguid;
    }

    public BreaksInfo(String id, String name) {
        m_sID = id;
        m_sName = name;
    }

    @Override
    public Object getKey() {
        return m_sID;
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
    public String toString(){
        return m_sName;
    }
}
