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


package uk.kalc.pos.printer.ticket;

import java.awt.Font;
import java.awt.geom.AffineTransform;


public class BasicTicketForPrinter extends BasicTicket {

        private static Font BASEFONT = new Font("Monospaced", Font.PLAIN, 7).deriveFont(AffineTransform.getScaleInstance(1.0, 1.40));
        private static int FONTHEIGHT = 12;
        private static double IMAGE_SCALE = 0.65;

    /**
     *
     * @return
     */
    @Override
    protected Font getBaseFont() {
        return BASEFONT;
    }

    /**
     *
     * @return
     */
    @Override
    protected int getFontHeight() {
        return FONTHEIGHT;
    }

    /**
     *
     * @return
     */
    @Override    protected double getImageScale() {
        return IMAGE_SCALE;
      }
  }