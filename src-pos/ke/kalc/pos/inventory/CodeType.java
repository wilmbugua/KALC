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

import ke.kalc.data.loader.IKeyed;

public class CodeType implements IKeyed {

    /**
     *
     */
    public static final CodeType EAN13 = new CodeType("EAN13", "EAN13");
    public static final CodeType EAN8 = new CodeType("EAN-8", "EAN-8");
    public static final CodeType UPCA = new CodeType("UPC-A", "UPC-A");
    public static final CodeType UPCE = new CodeType("UPC-E", "UPC-E");
    public static final CodeType CODE128 = new CodeType("CODE128", "CODE128");

    /**
     *
     */
    protected String m_sKey;

    /**
     *
     */
    protected String m_sValue;

    private CodeType(String key, String value) {
        m_sKey = key;
        m_sValue = value;
    }

    /**
     *
     * @return
     */
    @Override
    public Object getKey() {
        return m_sKey;
    }

    /**
     *
     * @return
     */
    public String getValue() {
        return m_sValue;
    }

    @Override
    public String toString() {
        return m_sValue;
    }
}
