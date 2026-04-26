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


package ke.kalc.pos.inventory;

import javax.swing.table.AbstractTableModel;
import ke.kalc.basic.BasicException;
import ke.kalc.data.user.BrowsableData;
import ke.kalc.format.Formats;

public class StockModel extends AbstractTableModel {
    
    private BrowsableData m_bd;
    private Formats[] m_formats;
    private boolean[] m_bedit;
    
    /** Creates a new instance of StockModel
     * @param bd
     * @param f
     * @param bedit */
    public StockModel(BrowsableData bd, Formats[] f, boolean[] bedit) {
        m_bd = bd;
        m_formats = f;
        m_bedit = bedit;
    }
    @Override
    public int getRowCount() {
        return m_bd.getSize();
    }
    @Override
    public int getColumnCount() {
        return m_formats.length;
    }
    @Override
    public Object getValueAt(int row, int column) {
        return m_formats[column].formatValue(
                ((Object[]) m_bd.getElementAt(row))[column]);
    }     
    @Override
    public boolean isCellEditable(int row, int column) {
        return m_bedit[column];
    }
    @Override
    public void setValueAt(Object aValue, int row, int column) {
        Object[] record = (Object[]) m_bd.getElementAt(row);
        try {
            record[column] = m_formats[column].parseValue((String) aValue);           
            m_bd.updateRecord(row, record);
        } catch (BasicException e) {           
        }
    }
}
