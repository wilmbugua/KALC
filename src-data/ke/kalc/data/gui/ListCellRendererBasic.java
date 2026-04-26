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


package ke.kalc.data.gui;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import ke.kalc.data.loader.IRenderString;

/**
 *
 *   
 */
public class ListCellRendererBasic extends DefaultListCellRenderer {
    
    private IRenderString m_renderer;
    
    /** Creates a new instance of ListCellRendererBasic
     * @param renderer */
    public ListCellRendererBasic(IRenderString renderer) {
        m_renderer = renderer;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);
        
        String s = m_renderer.getRenderString(value);
        setText((s == null || s.equals("")) ? " " : s); // Un espacio en caso de nulo para que no deja la celda chiquitita.
        return this;
    }        
 
}
