/*
**    KALC POS  - KALC POS
**
**    Copyright (c)2015-2021
**    
**    KALC and previous contributing parties (Unicenta & Openbravo)
**    http://www.KALC.co.uk
**
**    This file is part of KALC POS Version KALC V1.4.0
**    
**
**
*/

package ke.kalc.editor;

/**
 *
 *   
 */
public interface EditorKeys {
    
    /**
     *
     */
    public final static int MODE_STRING = 0;

    /**
     *
     */
    public final static int MODE_DOUBLE = 1;

    /**
     *
     */
    public final static int MODE_DOUBLE_POSITIVE = 2;

    /**
     *
     */
    public final static int MODE_INTEGER = 3;

    /**
     *
     */
    public final static int MODE_INTEGER_POSITIVE = 4;
    
    /**
     *
     * @param e
     * @param imode
     */
    public void setActive(EditorComponent e, int imode);

    /**
     *
     * @param e
     */
    public void setInactive(EditorComponent e);
}
