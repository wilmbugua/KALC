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

import java.awt.Image;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 *
 * @author John
 */
public class ExtendedJButton extends JButton {

    private int btnChoice = -1;

    public ExtendedJButton(String text) {
        super(text);
    }

    public ExtendedJButton(String text, String icon) {
        super("  " + text);
        try {
            Image img = ImageIO.read(getClass().getResource(icon));
            setIcon(new ImageIcon(img));
        } catch (Exception ex) {

        }
    }

    public ExtendedJButton(String text, int choice) {
        super(text);
        btnChoice = choice;
    }

    public ExtendedJButton(String text, String icon, int choice) {
        super("  " + text);
        setVerticalTextPosition(JButton.CENTER);
        setHorizontalTextPosition(JButton.RIGHT);
        try {
            Image img = ImageIO.read(getClass().getResource(icon));
            setIcon(new ImageIcon(img));
        } catch (Exception ex) {

        }
        btnChoice = choice;
    }
      
    
    public int getBtnChoice() {
        return btnChoice;
    }
}
