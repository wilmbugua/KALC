/*
**    KALC POS  - KALC POS
**
**    Copyright (c)2015-2021
**    
**    KALC and previous contributing parties
**    http://www.kalc.co.ke
**
**    This file is part of KALC POS Version KALC V1.4.0
**    
**
**
*/

package ke.kalc.editor;

import ke.kalc.format.Formats;

/**
 *
 *   
 */
public class JEditorIntegerPositive extends JEditorNumber {
    
    /** Creates a new instance of JEditorIntegerPositive */
    public JEditorIntegerPositive() {
    }
    
    /**
     *
     * @return
     */
    protected Formats getFormat() {
        return Formats.INT;
    }

    /**
     *
     * @return
     */
    protected int getMode() {
        return EditorKeys.MODE_INTEGER_POSITIVE;
    }      
}
