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


package ke.kalc.pos.printer.escpos;

import ke.kalc.pos.printer.DeviceTicket;


public class DeviceDisplaySurePOS extends DeviceDisplaySerial {
    
    private UnicodeTranslator trans;
    
    /**
     *
     * @param display
     */
    public DeviceDisplaySurePOS(PrinterWritter display) { 
        trans = new UnicodeTranslatorSurePOS();
        init(display);                
    }
   
    /**
     *
     */
    @Override
    public void initVisor() {
        display.write(new byte[]{0x00, 0x01}); // IBM Mode
        display.write(new byte[]{0x02}); // Set the code page
        display.write(trans.getCodeTable());
        display.write(new byte[]{0x11}); // HIDE CURSOR
        display.write(new byte[]{0x14}); // HIDE CURSOR
        display.write(new byte[]{0x10, 0x00}); // VISOR HOME
        display.flush();
    }

    /**
     *
     */
    @Override
    public void repaintLines() {
        display.write(new byte[]{0x10, 0x00}); // VISOR HOME
        display.write(trans.transString(DeviceTicket.alignLeft(m_displaylines.getLine1(), 20)));
        display.write(new byte[]{0x10, 0x14});
        display.write(trans.transString(DeviceTicket.alignLeft(m_displaylines.getLine2(), 20)));        
        display.flush();
    }
}
