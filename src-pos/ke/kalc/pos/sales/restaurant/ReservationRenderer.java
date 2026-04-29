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


package ke.kalc.pos.sales.restaurant;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import ke.kalc.globals.IconFactory;


public class ReservationRenderer extends DefaultListCellRenderer {
                
    private Icon icocustomer;

    public ReservationRenderer() {

      //  icocustomer = new ImageIcon(getClass().getClassLoader().getResource("customer_sml.png"));
        icocustomer =IconFactory.getIcon("timer.png");
        
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);
        setText(value.toString());
        setIcon(icocustomer);
        return this;
    }      
}
