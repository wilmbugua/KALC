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


package ke.kalc.custom;

import java.awt.Font;
import javax.swing.Icon;
import javax.swing.JLabel;

/**
 *
 * @author John
 */
public class CustomJLabel extends JLabel {

    public CustomJLabel(String text, Font font) {
        super();
        setText(text);
        setFont(font);
        setFocusable(false);
    }

    public CustomJLabel(String text, Icon icon) {
        super();
        setText(text);
        setIcon(icon);
        setFocusable(false);
    }
    
        public CustomJLabel(String text, Icon icon, Font font) {
        super();
        setText(text);
        setIcon(icon);
        setFont(font);
        setFocusable(false);
    }
}
