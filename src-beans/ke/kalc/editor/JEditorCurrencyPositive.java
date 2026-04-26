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
public class JEditorCurrencyPositive extends JEditorNumber {
    
    /** Creates a new instance of JEditorCurrencyPositive */
    public JEditorCurrencyPositive() {
    }
    
    /**
     *
     * @return
     */
    @Override
    protected Formats getFormat() {
        return Formats.CURRENCY;

    }

    /**
     *
     * @return
     */
    @Override
    protected int getMode() {
        return EditorKeys.MODE_DOUBLE_POSITIVE;
    }      
}
