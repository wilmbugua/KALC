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
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *   
 */
public class PrintItemLine implements PrintItem {

    /**
     *
     */
    protected Font font;

    /**
     *
     */
    protected int fontheight;

    /**
     *
     */
    protected int textsize;

    /**
     *
     */
    protected List<StyledText> m_atext;

    /** Creates a new instance of PrinterItemLine
     * @param textsize
     * @param font
     * @param fontheight */
    public PrintItemLine(int textsize, Font font, int fontheight) {
        this.textsize = textsize;
        this.font = font;
        this.fontheight = fontheight;


        m_atext = new ArrayList<>();
    }

    /**
     *
     * @param style
     * @param text
     */
    public void addText(int style, String text) {
        m_atext.add(new StyledText(style, text));
    }

    /**
     *
     * @param g
     * @param x
     * @param y
     * @param width
     */
    @Override
    public void draw(Graphics2D g, int x, int y, int width) {

        MyPrinterState ps = new MyPrinterState(textsize);
        float left = x;
        for (int i = 0; i < m_atext.size(); i++) {
            StyledText t = m_atext.get(i);
            g.setFont(ps.getFont(font, t.style));
            g.drawString(t.text, left, (float) y);
            left += g.getFontMetrics().getStringBounds(t.text, g).getWidth();
        }
    }

    /**
     *
     * @return
     */
    @Override
    public int getHeight() {
        return fontheight * MyPrinterState.getLineMult(textsize);
    }

    /**
     *
     */
    protected static class StyledText {

        /**
         *
         * @param style
         * @param text
         */
        public StyledText(int style, String text) {
            this.style = style;
            this.text = text;
        }

        /**
         *
         */
        public int style;

        /**
         *
         */
        public String text;
    }
}
