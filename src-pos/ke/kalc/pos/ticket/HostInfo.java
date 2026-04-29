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


package ke.kalc.pos.ticket;

import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.IKeyed;
import ke.kalc.data.loader.SerializerRead;

public class HostInfo implements IKeyed {

    //MONEY     HOST    HOSTSEQUENCE    DATESTART       DATEEND
    //private static final long serialVersionUID = 8612449444103L;
    private String m_sMoney;
    private String m_sHost;
    private String m_Hostsequence;

    /** Creates new CategoryInfo
     * @param money
     * @param host
     * @param hostsequence */
    public HostInfo(String money, String host, String hostsequence) {
       
        m_sMoney = host; // hack to search by hostname
        m_sHost = host;
        m_Hostsequence = hostsequence;
    }

    @Override
    public Object getKey() {
        return m_sMoney;
    }
   
    public String getHostsequence() {
        return m_Hostsequence;
    }

    public void setHostsequence(String m_Hostsequence) {
        this.m_Hostsequence = m_Hostsequence;
    }

    public String getHost() {
        return m_sHost;
    }

    public void setHost(String m_sHost) {
        this.m_sHost = m_sHost;
    }

    public String getMoney() {
        return m_sMoney;
    }

    public void setMoney(String m_sMoney) {
        this.m_sMoney = m_sMoney;
    }

    @Override
    public String toString() {
        return m_sHost;
    }

    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
    @Override
    public Object readValues(DataRead dr) throws BasicException {
            return new HostInfo(dr.getString(1), dr.getString(2), dr.getString(3));
        }
        };
    }
}

