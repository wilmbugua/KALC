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


package ke.kalc.pos.printer.ticket;

import java.awt.Font;
import java.awt.geom.AffineTransform;

/**
 *
 *   
 */
public class BasicTicketForScreen extends BasicTicket {

    private static Font BASEFONT = new Font("Monospaced", Font.PLAIN, 12).deriveFont(AffineTransform.getScaleInstance(1.0, 1.40));
    private static int FONTHEIGHT = 16;
    //private static double IMAGE_SCALE = .85;
    private static double IMAGE_SCALE = 1.2;
  

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
    @Override
    protected double getImageScale() {
        return IMAGE_SCALE;
    }
}