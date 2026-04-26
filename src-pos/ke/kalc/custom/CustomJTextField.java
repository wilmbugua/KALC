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

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JTextField;

/**
 *
 * @author John
 */
    public class CustomJTextField extends JTextField {

        public CustomJTextField(Dimension dimension, Font font) {
            super();
            this.setText("");
            this.setFont(font);
            this.setPreferredSize(dimension);
        }
    }
