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


package ke.kalc.pos.epm;


public class Break {

    private String m_sId;
    private String m_sName;
    private String m_sNotes;
    private boolean m_sVisible;
    private String m_sSiteGuid;

    /**
     *
     * @param id
     * @param name
     * @param notes
     * @param visible
     */
    public Break(String id, String name, String notes,  boolean visible) {
        m_sId = id;
        m_sName = name;
        m_sNotes = notes;
        m_sVisible = visible;
    }

        public Break(String id, String name, String notes,  boolean visible, String siteguid) {
        m_sId = id;
        m_sName = name;
        m_sNotes = notes;
        m_sVisible = visible;
        m_sSiteGuid = siteguid;
    }

    public String getSiteGuid() {
        return m_sSiteGuid;
    }

    public void setSiteGuid(String siteGuid) {
        this.m_sSiteGuid = siteGuid;
    }
    
    public String getId() {
        return m_sId;
    }

    public void setId(String Id) {
        this.m_sId = Id;
    }

    public String getName() {
        return m_sName;
    }

    public void setName(String Name) {
        this.m_sName = Name;
    }

    public String getNotes() {
        return m_sNotes;
    }

    public void setNotes(String Notes) {
        this.m_sNotes = Notes;
    }

    public boolean isVisible() {
        return m_sVisible;
    }

    public void setVisible(boolean Visible) {
        this.m_sVisible = Visible;
    }
}
