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
