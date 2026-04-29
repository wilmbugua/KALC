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


package ke.kalc.pos.panels;

import ke.kalc.data.loader.IKeyed;
import ke.kalc.pos.forms.AppLocal;

public class ComboItemLocal implements IKeyed {

    /**
     *
     */
    protected Integer m_iKey;

    /**
     *
     */
    protected String m_sKeyValue;

    /**
     *
     * @param iKey
     * @param sKeyValue
     */
    public ComboItemLocal(Integer iKey, String sKeyValue) {
        m_iKey = iKey;
        m_sKeyValue = sKeyValue;
    }

    /**
     *
     * @return
     */
    @Override
    public Object getKey() {
        return m_iKey;
    }

    /**
     *
     * @return
     */
    public Object getValue() {
        return m_sKeyValue;
    }
    @Override
    public String toString() {
        return AppLocal.getIntString(m_sKeyValue);
    }
} 
