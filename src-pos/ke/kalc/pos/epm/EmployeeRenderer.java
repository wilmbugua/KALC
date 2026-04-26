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


package ke.kalc.pos.epm;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import ke.kalc.globals.IconFactory;


public class EmployeeRenderer extends DefaultListCellRenderer {
                
    private Icon icoemployee;

    /** Creates a new instance of EmployeeRenderer */
    public EmployeeRenderer() {
        icoemployee = IconFactory.getIcon("user.png");
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);
        setText(value.toString());
        setIcon(icoemployee);
        return this;
    }      
}
