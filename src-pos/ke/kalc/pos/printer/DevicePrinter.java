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

import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 *
 *
 */
public interface DevicePrinter {

    public static final int SIZE_0 = 0;
    public static final int SIZE_1 = 1;
    public static final int SIZE_2 = 2;
    public static final int SIZE_3 = 3;
    public static final int STYLE_PLAIN = 0;
    public static final int STYLE_BOLD = 1;
    public static final int STYLE_UNDERLINE = 2;
    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_RIGHT = 1;
    public static final int ALIGN_CENTER = 2;
    public static final String BARCODE_EAN13 = "EAN13";
    public static final String BARCODE_EAN8 = "EAN8";
    public static final String BARCODE_CODE128 = "CODE128";
    public static final String BARCODE_CODE39 = "CODE39";
    public static final String BARCODE_UPCA = "UPC-A";
    public static final String BARCODE_UPCE = "UPC-E";
    public static final String POSITION_BOTTOM = "bottom";
    public static final String POSITION_NONE = "none";
    public String getPrinterName();
    public String getPrinterDescription();
    public JComponent getPrinterComponent();

    
    public void reset();

    // INTERFAZ PRINTER

    /**
     *
     */
    public void beginReceipt();
    public void printImage(BufferedImage image);
    public void printLogo(Byte iNumber);
    public Boolean printBarCode(String type, String position, String code);
    public void beginLine(int iTextSize);
    public void printText(int iStyle, String sText);
    public void endLine();
    public void endReceipt();
    public void openDrawer();

}
