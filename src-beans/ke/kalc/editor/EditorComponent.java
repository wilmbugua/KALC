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

import java.awt.Component;

/**
 *
 *   
 */
public interface EditorComponent {
    
    /**
     *
     * @param ed
     */
    public void addEditorKeys(EditorKeys ed);    

    /**
     *
     * @return
     */
    public Component getComponent();
    
    /**
     *
     */
    public void deactivate();

    /**
     *
     * @param c
     */
    public void typeChar(char c);

    /**
     *
     * @param c
     */
    public void transChar(char c);
}
