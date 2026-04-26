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

import java.io.Serializable;

public class UserInfo implements Serializable {

    private static final long serialVersionUID = 7537578737839L;
    private final String m_sId;
    private final String m_sName;

    /**
     * Creates a new instance of UserInfoBasic
     *
     * @param id
     * @param name
     */
    public UserInfo(String id, String name) {
        m_sId = id;
        m_sName = name;
    }

    /**
     *
     * @return
     */
    public String getId() {
        return m_sId;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return m_sName;
    }

    @Override
    public String toString() {
        return getName();
    }

}
