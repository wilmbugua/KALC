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


public class JEditorStringNumber extends JEditorText {
    
    /** Creates a new instance of JEditorStringNumber */
    public JEditorStringNumber() {
        super();
    }
    
    /**
     *
     * @return
     */
    protected int getMode() {
        return EditorKeys.MODE_INTEGER_POSITIVE;
    }

    /**
     *
     * @return
     */
    protected int getStartMode() {
        return MODE_123;
    }    
}
