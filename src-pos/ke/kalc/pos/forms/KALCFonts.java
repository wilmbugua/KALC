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

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import ke.kalc.globals.SystemProperty;

public class KALCFonts {

    public static Font DEFAULTFONT;
    public static Font DEFAULTFONTBOLD;
    public static Font DEFAULTBUTTONFONT;
    public static Font KALCFONT;
    public static Font KALCFONTBOLD;

    public static void setFont(String fontName) {

        KALCFONT = new Font("Courgette", Font.PLAIN, 14);
        KALCFONTBOLD = new Font("Courgette", Font.BOLD, 14);

        String font = SystemProperty.SWINGFONT;
        if (!SystemProperty.SWINGFONT.isBlank()) {
            if (Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()).contains(font)) {
                DEFAULTFONT = new Font(font, Font.PLAIN, SystemProperty.SWINGFONTSIZE);
                DEFAULTFONTBOLD = new Font(font, Font.BOLD, SystemProperty.SWINGFONTSIZE);
                DEFAULTBUTTONFONT = new Font(font, Font.BOLD, SystemProperty.SWINGFONTSIZE + 2);
                return;
            }
        }

        DEFAULTFONT = new Font(fontName, Font.PLAIN, 14);
        DEFAULTFONTBOLD = new Font(fontName, Font.BOLD, 14);
        DEFAULTBUTTONFONT = new Font(fontName, Font.BOLD, 16);

    }

}
