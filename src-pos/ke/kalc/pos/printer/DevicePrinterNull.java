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


package ke.kalc.pos.printer;

import ke.kalc.pos.forms.AppLocal;

/**
 *
 *   
 */
public class DevicePrinterNull implements DevicePrinter {
    
    private String m_sName;
    private String m_sDescription;
    
    /** Creates a new instance of DevicePrinterNull */
    public DevicePrinterNull() {
        this(null);
    }
    
    /** Creates a new instance of DevicePrinterNull
     * @param desc */
    public DevicePrinterNull(String desc) {
        m_sName = AppLocal.getIntString("printer.null");
        m_sDescription = desc;
    }

    /**
     *
     * @return
     */
    @Override
    public String getPrinterName() {
        return m_sName;
    }    

    /**
     *
     * @return
     */
    @Override
    public String getPrinterDescription() {
        return m_sDescription;
    }        

    /**
     *
     * @return
     */
    @Override
    public javax.swing.JComponent getPrinterComponent() {
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
     * @param type
     * @param position
     * @param code
     */
    @Override
    public Boolean printBarCode(String type, String position, String code) {        
        return true;
    }    

    /**
     *
     * @param image
     */
    @Override
    public void printImage(java.awt.image.BufferedImage image) {
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
    }   

    /**
     *
     */
    @Override
    public void endLine() {
    }

    /**
     *
     */
    @Override
    public void endReceipt() {
    }

    /**
     *
     */
    @Override
    public void openDrawer() {
    }

    /**
     *
     */
    @Override
    public void printLogo(Byte iNumber) {
        
    }

    }
