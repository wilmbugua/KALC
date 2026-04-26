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

import javax.swing.Icon;


public class NullIcon implements Icon {
    
    private int m_iWidth;
    private int m_iHeight;
    
    /** Creates a new instance of NullIcon
     * @param width
     * @param height */
    public NullIcon(int width, int height) {
        m_iWidth = width;
        m_iHeight = height;
    }
    
    @Override
    public int getIconHeight() {
        return m_iHeight;
    }
    
    @Override
    public int getIconWidth() {
        return m_iWidth;
    }
    
    @Override
    public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {
    }
    
}
