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

import ke.kalc.format.Formats;

/**
 *
 *   
 */
public class JEditorDoublePositive extends JEditorNumber {
    
    /** Creates a new instance of JEditorDoublePositive */
    public JEditorDoublePositive() {
    }
    
    /**
     *
     * @return
     */
    protected Formats getFormat() {
        return Formats.DOUBLE;
    }

    /**
     *
     * @return
     */
    protected int getMode() {
        return EditorKeys.MODE_DOUBLE_POSITIVE;
    }       
}
