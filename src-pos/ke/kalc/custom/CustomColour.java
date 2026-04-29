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

import java.awt.Color;
import ke.kalc.globals.SystemProperty;

/**
 *
 * @author John
 */
public class CustomColour {

    public static Color getBorderColour() {
        if (SystemProperty.LAF.equalsIgnoreCase("com.jtattoo.plaf.hifi.HiFiLookAndFeel")) {
            return Color.WHITE;
        }
        return Color.BLACK;
    }

    public static Color getEnabledColour(Color color) {
        double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
        return y >= 128 ? Color.black : Color.white;
    }
}
