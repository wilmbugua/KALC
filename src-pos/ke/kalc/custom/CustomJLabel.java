/*
**    KALC POS  - Open Source Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous Openbravo POS related works   
**
**    https://www.KALC.co.uk
**   
**    KALC POS is free software: you can redistribute it and/or modify
**    it under the terms of the GNU General Public License as published by
**    the Free Software Foundation, either version 3 of the License, or
**    (at your option) any later version.
**
**    KALC POS is distributed in the hope that it will be useful,
**    but WITHOUT ANY WARRANTY; without even the implied warranty of
**    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
**    GNU General Public License for more details.
**
**    You should have received a copy of the GNU General Public License
**    along with KALC POS.  If not, see <http://www.gnu.org/licenses/>
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
