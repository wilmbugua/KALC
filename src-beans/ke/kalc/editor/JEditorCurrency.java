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
public class JEditorCurrency extends JEditorNumber {
    
	private static final long serialVersionUID = 5096754100573262803L;
	
	/** Creates a new instance of JEditorCurrency */
    public JEditorCurrency() {
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
        return EditorKeys.MODE_DOUBLE;
    }  
}
