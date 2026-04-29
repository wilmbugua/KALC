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


package ke.kalc.pos.printer.ticket;

import java.awt.Graphics2D;

/**
 *
 *   
 */
public interface PrintItem {
    
    /**
     *
     * @return
     */
    public int getHeight();

    /**
     *
     * @param g
     * @param x
     * @param y
     * @param width
     */
    public void draw(Graphics2D g, int x, int y, int width);
}
