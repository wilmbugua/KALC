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

import javax.swing.Icon;
import javax.swing.JLabel;
import ke.kalc.data.user.DirtyListener;
import ke.kalc.data.user.DirtyManager;
import ke.kalc.globals.IconFactory;

/**
 *
 *   
 */
public class JLabelDirty extends JLabel {
    
    private static Icon m_IconModif = null;
    private static Icon m_IconNull = null;

    /** Creates a new instance of JDirtyPicture
     * @param dm */
    public JLabelDirty(DirtyManager dm) {
        
        if (m_IconModif == null) {
            m_IconModif = IconFactory.getIcon("edit.png");
        }
        if (m_IconNull == null) {
            m_IconNull = new NullIcon(16, 16);
        }
        
        dm.addDirtyListener(new DirtyListener() {
            public void changedDirty(boolean bDirty) {
                setIcon(bDirty ? m_IconModif : m_IconNull);
            }
        });
    }  
}
