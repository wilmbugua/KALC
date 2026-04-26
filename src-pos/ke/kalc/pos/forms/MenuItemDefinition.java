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


package ke.kalc.pos.forms;

import java.awt.Dimension;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.SwingConstants;

public class MenuItemDefinition implements MenuElement {
    
    private Action act;
    
    /**
     *
     * @param act
     */
    public MenuItemDefinition(Action act) {
        this.act = act;
    }
    
    /**
     *
     * @param menu
     */
    @Override
    public void addComponent(JPanelMenu menu) {        
        JButton btn = new JButton(act);         
        btn.setFocusPainted(false);
        btn.setFocusable(false);
        btn.setRequestFocusEnabled(false);
        btn.setHorizontalAlignment(SwingConstants.LEADING);
        btn.setPreferredSize(new Dimension(150, 40));              
        menu.addEntry(btn);
    }
}
