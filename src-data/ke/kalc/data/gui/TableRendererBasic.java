/*
**    KALC POS  - Professional Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous KALC POS related works   
**
**    https://www.kalc.co.ke
**   
**
*/


package ke.kalc.data.gui;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import ke.kalc.format.Formats;

/**
 *
 *   
 */
public class TableRendererBasic extends DefaultTableCellRenderer {
    
    private Formats[] m_aFormats;
    
    /** Creates a new instance of TableRendererBasic
     * @param aFormats */
    public TableRendererBasic(Formats[] aFormats) {
        m_aFormats = aFormats;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){

        JLabel aux = (JLabel) super.getTableCellRendererComponent(table, null, isSelected, hasFocus, row, column);
        
        aux.setText(m_aFormats[column].formatValue(value));
        aux.setHorizontalAlignment(m_aFormats[column].getAlignment());

        return aux;
    }    
}
