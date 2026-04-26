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


package ke.kalc.data.loader;


public class KeyGetterBasic implements IKeyGetter {
    
    private int [] m_aElems;
    
    /** Creates a new instance of KeyGetterBasic
     * @param aElems */
    public KeyGetterBasic(int[] aElems) {
        m_aElems = aElems;
    }
    
    /**
     *
     * @param value
     * @return
     */
    public Object getKey(Object value) {
        if (value == null) {
            return null;
        } else {
            Object[] avalue = (Object []) value;
            Object[] akey = new Object[m_aElems.length];
            for (int i = 0; i < m_aElems.length; i++) {
                akey[i] = avalue[m_aElems[i]];
            }
            return akey;
        }
    }   
}
