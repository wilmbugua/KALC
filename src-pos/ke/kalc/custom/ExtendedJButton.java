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
