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

import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import ke.kalc.pos.printer.DevicePrinter;
import ke.kalc.pos.printer.TicketPrinterException;

/**
 *
 *   
 */
public class DevicePrinterPlain implements DevicePrinter  {
    
    private static final byte[] NEW_LINE = {0x0D, 0x0A}; // Print and carriage return
      
    private PrinterWritter out;
    private UnicodeTranslator trans;
    
    // Creates new TicketPrinter

    /**
     *
     * @param CommOutputPrinter
     * @throws TicketPrinterException
     */
        public DevicePrinterPlain(PrinterWritter CommOutputPrinter) throws TicketPrinterException {

        out = CommOutputPrinter;
        trans = new UnicodeTranslatorStar(); // The star translator stands for the 437 int char page
    }
   
    /**
     *
     * @return
     */
    @Override
    public String getPrinterName() {
        return "Plain";
    }

    /**
     *
     * @return
     */
    @Override
    public String getPrinterDescription() {
        return null;
    }   

    /**
     *
     * @return
     */
    @Override
    public JComponent getPrinterComponent() {
        return null;
    }

    /**
     *
     */
    @Override
    public void reset() {
    }
    
    /**
     *
     */
    @Override
    public void beginReceipt() {
    }
    
    /**
     *
     * @param image
     */
    @Override
    public void printImage(BufferedImage image) {
    }
    
    /**
     *
     */
    @Override
    public void printLogo(Byte iNumber){
        
    }
    
    /**
     *
     * @param type
     * @param position
     * @param code
     */
    @Override
    public Boolean printBarCode(String type, String position, String code) {        
        if (! DevicePrinter.POSITION_NONE.equals(position)) {                
            out.write(code);
            out.write(NEW_LINE);
            return true;
        }
        return false;
    }
    
    /**
     *
     * @param iTextSize
     */
    @Override
    public void beginLine(int iTextSize) {
    }
    
    /**
     *
     * @param iStyle
     * @param sText
     */
    @Override
    public void printText(int iStyle, String sText) {
        out.write(trans.transString(sText));
    }
    
    /**
     *
     */
    @Override
    public void endLine() {
        out.write(NEW_LINE);
    }
    
    /**
     *
     */
    @Override
    public void endReceipt() {       
        out.write(NEW_LINE);
        out.write(NEW_LINE);
        out.write(NEW_LINE);
        out.write(NEW_LINE);
        out.write(NEW_LINE);
        out.flush();
    }
    
    /**
     *
     */
    @Override
    public void openDrawer() {
    }

    }

