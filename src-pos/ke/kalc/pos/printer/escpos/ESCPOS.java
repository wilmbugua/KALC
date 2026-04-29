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

/**
 *
 *
 */
public class ESCPOS {

    public static final byte[] INIT = {0x1B, 0x40};
    public static final byte[] SELECT_PRINTER = {0x1B, 0x3D, 0x01};
    public static final byte[] SELECT_DISPLAY = {0x1B, 0x3D, 0x02};
    public static final byte[] HT = {0x09}; // Horizontal Tab
    public static final byte[] FF = {0x0C}; // 
    public static final byte[] CHAR_FONT_0 = {0x1B, 0x4D, 0x00};
    public static final byte[] CHAR_FONT_1 = {0x1B, 0x4D, 0x01};
    public static final byte[] CHAR_FONT_2 = {0x1B, 0x4D, 0x30};
    public static final byte[] CHAR_FONT_3 = {0x1B, 0x4D, 0x31};
    public static final byte[] BAR_HEIGHT = {0x1D, 0x68, 0x20};
    public static final byte[] BAR_POSITIONDOWN = {0x1D, 0x48, 0x02};
    public static final byte[] BAR_POSITIONNONE = {0x1D, 0x48, 0x00};
    public static final byte[] BAR_HRIFONT1 = {0x1D, 0x66, 0x01};
    public static final byte[] BAR_CODE02 = {0x1D, 0x6B, 0x02}; // 12 numeros fijos
    public static final byte[] VISOR_HIDE_CURSOR = {0x1F, 0x43, 0x00};
    public static final byte[] VISOR_SHOW_CURSOR = {0x1F, 0x43, 0x01};
    public static final byte[] VISOR_HOME = {0x0B};
    public static final byte[] VISOR_CLEAR = {0x0C};
    public static final byte[] CODE_TABLE_00 = {0x1B, 0x74, 0x00};
    public static final byte[] CODE_TABLE_13 = {0x1B, 0x74, 0x13};
    public static final byte[] HWBARCODE_FIXED_WIDTH1 = {0x1b, 0x61, 0x01, 0x1D, 0x77, 0x01, 0x1D, 0x48, 0x02, 0x1D, 0x68, 0x20};
    public static final byte[] HWBARCODE_FIXED_WIDTH2 = {0x1b, 0x61, 0x01, 0x1D, 0x77, 0x02, 0x1D, 0x48, 0x02, 0x1D, 0x68, 0x20};
    public static final byte[] HWBARCODE_FIXED_WIDTH4 = {0x1b, 0x61, 0x01, 0x1D, 0x77, 0x04, 0x1D, 0x48, 0x02, 0x1D, 0x68, 0x20};
    public static final byte[] HWBARCODE = {0x1b, 0x61, 0x01, 0x1D, 0x48, 0x02, 0x1D, 0x68, 0x20};
    public static final byte[] TEST = {0x1D, 0x77, 0x04, 0x1D, 0x6B, 0x49, 0x07, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37};
    public static final byte[] BARCODE_UPCA = {0x1D, 0x6B, 0x00};
    public static final byte[] BARCODE_UPCE = {0x1D, 0x6B, 0x01};
    public static final byte[] BARCODE_EAN13 = {0x1D, 0x6B, 0x02};
    public static final byte[] BARCODE_EAN8 = {0x1D, 0x6B, 0x03};
    public static final byte[] BARCODE_CODE39 = {0x1D, 0x6B, 0x04};
    public static final byte[] BARCODE_CODE128 = {0x1D, 0x6B, 0x49};
    

    private ESCPOS() {
    }
}
