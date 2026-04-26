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
public class JEditorGiftCard extends JEditorText {

    protected String m_svalue;

    /**
     * Creates a new instance of JEditorPassword
     */
    public JEditorGiftCard() {
        super();
    }

    /**
     *
     * @return
     */
    protected final int getMode() {
        return EditorKeys.MODE_STRING;
    }

    /**
     *
     * @return
     */
    protected int getStartMode() {
        return MODE_123;
    }


}
