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


package ke.kalc.pos.panels;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import ke.kalc.basic.BasicException;
import ke.kalc.data.loader.DataField;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.Datas;


public class SQLTableModel extends AbstractTableModel {
    
    private List m_aRows;
    
    private DataField[] m_df;
    private Datas[] m_classes;
    
    /** Creates a new instance of SQLTableModel
     * @param df */
    public SQLTableModel(DataField[] df) {
        m_aRows = new ArrayList();

        m_df = df;
        m_classes = new Datas[df.length];
        for (int i = 0; i < df.length; i++) {
            switch (df[i].Type) {
                case Types.INTEGER:
                case Types.BIGINT:
                case Types.SMALLINT:
                case Types.TINYINT:
                    m_classes[i] = Datas.INT;
                    break;
                case Types.BIT:
                case Types.BOOLEAN:
                    m_classes[i] = Datas.BOOLEAN;
                    break;
                case Types.DECIMAL:
                case Types.DOUBLE:
                case Types.FLOAT:
                case Types.REAL:
                case Types.NUMERIC:
                    m_classes[i] = Datas.DOUBLE;
                    break;
                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                case Types.CLOB:
                    m_classes[i] = Datas.STRING;
                    break;
                case Types.DATE:
                case Types.TIME:
                case Types.TIMESTAMP:
                    m_classes[i] = Datas.TIMESTAMP;
                    break;
                case Types.BINARY:
                case Types.VARBINARY:
                case Types.LONGVARBINARY:
                case Types.BLOB:
                    m_classes[i] = Datas.BYTES;
                    break;
                case Types.ARRAY:                    
                case Types.DATALINK:
                case Types.DISTINCT:
                case Types.JAVA_OBJECT:
                case Types.NULL:
                case Types.OTHER:
                case Types.REF:
                case Types.STRUCT:
                default:
                    m_classes[i] = Datas.OBJECT;
                    break;
            }
        }
    }

    /**
     *
     * @param dr
     * @throws BasicException
     */
    public void addRow(DataRead dr) throws BasicException {
        
        Object[] m_values = new Object[m_classes.length];
        for (int i = 0; i < m_classes.length; i++) {
            m_values[i] = m_classes[i].getValue(dr, i + 1);
        }
         m_aRows.add(m_values);
    }     

    /**
     *
     * @param row
     * @return
     */
    public String getColumnString(int row) {
        Object [] rowvalues = (Object[]) m_aRows.get(row);
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < rowvalues.length; i++) {
            if (i > 0) {
                s.append(", ");
            }
            s.append(m_classes[i].toString(rowvalues[i]));
        }
        return s.toString();
    }
    
    @Override
    public Class getColumnClass(int columnIndex) {
        return m_classes[columnIndex].getClassValue();
    }
    @Override
    public String getColumnName(int columnIndex) {
        return m_df[columnIndex].Name;
    }    
    @Override
    public int getRowCount() {
        return m_aRows.size();
    }
    @Override
    public int getColumnCount() {
        return m_df.length;
    }
    @Override
    public Object getValueAt(int row, int column) {
        Object [] rowvalues = (Object[]) m_aRows.get(row);
        return rowvalues[column];
    }  
}
