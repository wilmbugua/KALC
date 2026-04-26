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
package ke.kalc.pos.printer;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import ke.kalc.commons.utils.TerminalInfo;
import ke.kalc.pos.forms.AppConfig;
import ke.kalc.pos.forms.AppProperties;
import ke.kalc.pos.printer.escpos.CodesEpson;
import ke.kalc.pos.printer.escpos.CodesIthaca;
import ke.kalc.pos.printer.escpos.CodesStar;
import ke.kalc.pos.printer.escpos.CodesSurePOS;
import ke.kalc.pos.printer.escpos.CodesTMU220;
import ke.kalc.pos.printer.escpos.DeviceDisplayESCPOS;
import ke.kalc.pos.printer.escpos.DeviceDisplaySurePOS;
import ke.kalc.pos.printer.escpos.DevicePrinterESCPOS;
import ke.kalc.pos.printer.escpos.DevicePrinterPlain;
import ke.kalc.pos.printer.escpos.PrinterWritter;
import ke.kalc.pos.printer.escpos.PrinterWritterFile;
import ke.kalc.pos.printer.escpos.PrinterWritterRXTX;
import ke.kalc.pos.printer.escpos.PrinterWritterRaw;
import ke.kalc.pos.printer.escpos.UnicodeTranslatorEur;
import ke.kalc.pos.printer.escpos.UnicodeTranslatorInt;
import ke.kalc.pos.printer.escpos.UnicodeTranslatorStar;
import ke.kalc.pos.printer.escpos.UnicodeTranslatorSurePOS;
import ke.kalc.pos.printer.javapos.DeviceDisplayJavaPOS;
import ke.kalc.pos.printer.javapos.DeviceFiscalPrinterJavaPOS;
import ke.kalc.pos.printer.javapos.DevicePrinterJavaPOS;
import ke.kalc.pos.printer.printer.DevicePrinterPrinter;
import ke.kalc.pos.printer.screen.DeviceDisplayScreen;
import ke.kalc.pos.printer.screen.DeviceDisplayWindow;
import ke.kalc.pos.printer.screen.DevicePrinterPanel;
import ke.kalc.pos.util.StringParser;

/**
 *
 *
 */
public class DeviceTicket {

    private static final Logger logger = Logger.getLogger("ke.kalc.pos.printer.DeviceTicket");

    private DeviceFiscalPrinter m_deviceFiscal;
    private DeviceDisplay m_devicedisplay;
    private DevicePrinter m_nullprinter;
    private Map<String, DevicePrinter> m_deviceprinters;
    private List<DevicePrinter> m_deviceprinterslist;

    public DeviceTicket(AppProperties props) {

        m_deviceFiscal = new DeviceFiscalPrinterNull();

        m_devicedisplay = new DeviceDisplayNull();

        m_nullprinter = new DevicePrinterNull();
        m_deviceprinters = new HashMap<>();
        m_deviceprinterslist = new ArrayList<>();

        DevicePrinter p = new DevicePrinterPanel(props);
        m_deviceprinters.put("receiptprinter", p);
        m_deviceprinterslist.add(p);
    }

    /**
     *
     * @param parent
     * @param props
     */
    public DeviceTicket(Component parent, AppProperties props) {

        PrinterWritterPool pws = new PrinterWritterPool();

        StringParser sf = new StringParser(AppConfig.getString("machine.fiscalprinter"));
        String sFiscalType = sf.nextToken(':');
        String sFiscalParam1 = sf.nextToken(',');
        try {
            if ("javapos".equals(sFiscalType)) {
                m_deviceFiscal = new DeviceFiscalPrinterJavaPOS(sFiscalParam1);
            } else {
                m_deviceFiscal = new DeviceFiscalPrinterNull();
            }
        } catch (TicketPrinterException e) {
            m_deviceFiscal = new DeviceFiscalPrinterNull(e.getMessage());
        }

        StringParser sd = new StringParser(TerminalInfo.getDisplay());
        String sDisplayType = sd.nextToken(':');
        String sDisplayParam1 = sd.nextToken(',');
        String sDisplayParam2 = sd.nextToken(',');

        if ("serial".equals(sDisplayType) || "rxtx".equals(sDisplayType) || "file".equals(sDisplayType)) {
            sDisplayParam2 = sDisplayParam1;
            sDisplayParam1 = sDisplayType;
            sDisplayType = "epson";
        }

        try {

            switch (sDisplayType) {
                case "dual screen":
                    m_devicedisplay = new DeviceDisplayScreen();
                    break;
                case "window":
                    m_devicedisplay = new DeviceDisplayWindow();
                    break;
                case "epson":
                    m_devicedisplay = new DeviceDisplayESCPOS(pws.getPrinterWritter(sDisplayParam1, sDisplayParam2), new UnicodeTranslatorInt());
                    break;
                case "surepos":
                    m_devicedisplay = new DeviceDisplaySurePOS(pws.getPrinterWritter(sDisplayParam1, sDisplayParam2));
                    break;
                case "ld200":
                    m_devicedisplay = new DeviceDisplayESCPOS(pws.getPrinterWritter(sDisplayParam1, sDisplayParam2), new UnicodeTranslatorEur());
                    break;
                case "javapos":
                    m_devicedisplay = new DeviceDisplayJavaPOS(sDisplayParam1);
                    break;
                default:
                    m_devicedisplay = new DeviceDisplayNull();
                    break;
            }
        } catch (TicketPrinterException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            m_devicedisplay = new DeviceDisplayNull(e.getMessage());
        }

        m_nullprinter = new DevicePrinterNull();

        m_deviceprinters = new HashMap<>();
        m_deviceprinterslist = new ArrayList<>();

        int iPrinterIndex = 1;
        String sPrinterIndex = Integer.toString(iPrinterIndex);
        String sprinter = TerminalInfo.getReceiptPrinter();

        while (sprinter != null && !"".equals(sprinter)) {

            StringParser sp = new StringParser(sprinter);
            String sPrinterType = sp.nextToken(':');
            String sPrinterParam1 = sp.nextToken(',');
            String sPrinterParam2 = sp.nextToken(',');

            if ("serial".equals(sPrinterType) || "rxtx".equals(sPrinterType) || "file".equals(sPrinterType)) {
                sPrinterParam2 = sPrinterParam1;
                sPrinterParam1 = sPrinterType;
                sPrinterType = "epson";
            }

            try {

                switch (sPrinterType) {
                    case "screen":
                        addPrinter(sPrinterIndex, new DevicePrinterPanel(props, iPrinterIndex));
                        break;
                    case "printer":
                        // backward compatibility
                        if (sPrinterParam2 == null || sPrinterParam2.equals("") || sPrinterParam2.equals("true")) {
                            sPrinterParam2 = "receipt";
                        } else if (sPrinterParam2.equals("false")) {
                            sPrinterParam2 = "standard";
                        }
                        addPrinter(sPrinterIndex, new DevicePrinterPrinter(parent, sPrinterParam1,
                                AppConfig.getInt("paper." + sPrinterParam2 + ".x"),
                                AppConfig.getInt("paper." + sPrinterParam2 + ".y"),
                                AppConfig.getInt("paper." + sPrinterParam2 + ".width"),
                                AppConfig.getInt("paper." + sPrinterParam2 + ".height"),
                                AppConfig.getString("paper." + sPrinterParam2 + ".mediasizename")));
                        break;
                    case "epson":
                        addPrinter(sPrinterIndex, new DevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2), new CodesEpson(), new UnicodeTranslatorInt()));
                        break;
                    case "tmu220":
                        addPrinter(sPrinterIndex, new DevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2), new CodesTMU220(), new UnicodeTranslatorInt()));
                        break;
                    case "star":
                        addPrinter(sPrinterIndex, new DevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2), new CodesStar(), new UnicodeTranslatorStar()));
                        break;
                    case "ithaca":
                        addPrinter(sPrinterIndex, new DevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2), new CodesIthaca(), new UnicodeTranslatorInt()));
                        break;
                    case "surepos":
                        addPrinter(sPrinterIndex, new DevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2), new CodesSurePOS(), new UnicodeTranslatorSurePOS()));
                        break;
                    case "plain":
                        addPrinter(sPrinterIndex, new DevicePrinterPlain(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2)));
                        break;
                    case "javapos":
                        addPrinter(sPrinterIndex, new DevicePrinterJavaPOS(sPrinterParam1, sPrinterParam2));
                        break;
                }
            } catch (TicketPrinterException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }

            iPrinterIndex++;
            sPrinterIndex = Integer.toString(iPrinterIndex);
            switch (iPrinterIndex) {
                case 2:
                    sprinter = TerminalInfo.getKitchenPrinter();
                    break;
                case 3:
                    sprinter = TerminalInfo.getKitchenPrinter2();
                    break;
                case 4:
                    sprinter = TerminalInfo.getKitchenPrinter3();
                    break;
                case 5:
                    sprinter = TerminalInfo.getBarPrinter();
                    break;
                case 6:
                    sprinter = TerminalInfo.getBarPrinter2();
                    break;
                case 7:
                    sprinter = TerminalInfo.getRemotePrinter();
                    break;
                case 8:
                    sprinter = TerminalInfo.getRemotePrinter2();
                    break;
                case 9:
                    sprinter = TerminalInfo.getRemotePrinter3();
                    break;
                case 10:
                    sprinter = TerminalInfo.getRemotePrinter4();
                    break;
                default:
                    sprinter = null;
                    break;

            }
        }
    }

    private void addPrinter(String sPrinterIndex, DevicePrinter p) {
        switch (sPrinterIndex) {
            case "1":
                m_deviceprinters.put("receiptprinter", p);
                m_deviceprinterslist.add(p);
                break;
            case "2":
                m_deviceprinters.put("printer1", p);
                m_deviceprinterslist.add(p);
                break;
            case "3":
                m_deviceprinters.put("printer2", p);
                m_deviceprinterslist.add(p);
                break;
            case "4":
                m_deviceprinters.put("printer3", p);
                m_deviceprinterslist.add(p);
                break;
            case "5":
                m_deviceprinters.put("printer4", p);
                m_deviceprinterslist.add(p);
                break;
            case "6":
                m_deviceprinters.put("printer5", p);
                m_deviceprinterslist.add(p);
                break;
            case "7":
                m_deviceprinters.put("printer6", p);
                m_deviceprinterslist.add(p);
                break;
            case "8":
                m_deviceprinters.put("printer7", p);
                m_deviceprinterslist.add(p);
                break;
            case "9":
                m_deviceprinters.put("printer8", p);
                m_deviceprinterslist.add(p);
                break;
            case "10":
                m_deviceprinters.put("printer9", p);
                m_deviceprinterslist.add(p);
                break;
            case "11":
                m_deviceprinters.put("printer10", p);
                m_deviceprinterslist.add(p);
                break;
        }

    }

    private static class PrinterWritterPool {

        private final Map<String, PrinterWritter> m_apool = new HashMap<>();

        public PrinterWritter getPrinterWritter(String con, String port) throws TicketPrinterException {

            String skey = con + "-->" + port;
            PrinterWritter pw = (PrinterWritter) m_apool.get(skey);
            if (pw == null) {

                switch (con) {
                    case "serial":
                    case "rxtx":
                        pw = new PrinterWritterRXTX(port);
                        m_apool.put(skey, pw);
                        break;
                    case "file":
                        pw = new PrinterWritterFile(port);
                        m_apool.put(skey, pw);
                        break;
                    case "usb":
                    case "raw":
                        pw = new PrinterWritterRaw(port);
                        m_apool.put(skey, pw);
                        break;
                    default:
                        throw new TicketPrinterException();
                }
            }
            return pw;
        }
    }

    /**
     *
     * @return Fiscal printer
     */
    public DeviceFiscalPrinter getFiscalPrinter() {
        return m_deviceFiscal;
    }

    /**
     *
     * @return Device display
     */
    public DeviceDisplay getDeviceDisplay() {
        return m_devicedisplay;
    }

    /**
     *
     * @param key
     * @return Device printer
     */
    public DevicePrinter getDevicePrinter(String key) {
        DevicePrinter printer = m_deviceprinters.get(key);
        return printer == null ? m_nullprinter : printer;
    }

    /**
     *
     * @return Device printer list
     */
    public List<DevicePrinter> getDevicePrinterAll() {
        return m_deviceprinterslist;
    }

    /**
     *
     * @param iSize
     * @param cWhiteChar
     * @return Spacing string length
     */
    public static String getWhiteString(int iSize, char cWhiteChar) {

        char[] cFill = new char[iSize];
        for (int i = 0; i < iSize; i++) {
            cFill[i] = cWhiteChar;
        }
        return new String(cFill);
    }

    /**
     *
     * @param iSize
     * @return Space sizing
     */
    public static String getWhiteString(int iSize) {

        return getWhiteString(iSize, ' ');
    }

    /**
     *
     * @param sLine
     * @param iSize
     * @return Barcode bar inter-spacing
     */
    public static String alignBarCode(String sLine, int iSize) {

        if (sLine.length() > iSize) {
            return sLine.substring(sLine.length() - iSize);
        } else {
            return getWhiteString(iSize - sLine.length(), '0') + sLine;
        }
    }

    public static String alignHWBarCode(String sLine, int iSize) {
        if (sLine.length() > iSize) {
            return sLine.substring(sLine.length() - iSize);
        } else {

            return getWhiteString(iSize - sLine.length(), '0') + sLine;
        }
    }

    /**
     *
     * @param sLine
     * @param iSize
     * @return Reduce spacing
     */
    public static String alignLeft(String sLine, int iSize) {

        if (sLine.length() > iSize) {
            return sLine.substring(0, iSize);
        } else {
            return sLine + getWhiteString(iSize - sLine.length());
        }
    }

    /**
     *
     * @param sLine
     * @param iSize
     * @return Add spacing
     */
    public static String alignRight(String sLine, int iSize) {

        if (sLine.length() > iSize) {
            return sLine.substring(sLine.length() - iSize);
        } else {
            return getWhiteString(iSize - sLine.length()) + sLine;
        }
    }

    /**
     *
     * @param sLine
     * @param iSize
     * @return Adjusts Left/Right spacing
     */
    public static String alignCenter(String sLine, int iSize) {

        if (sLine.length() > iSize) {
            return alignRight(sLine.substring(0, (sLine.length() + iSize) / 2), iSize);
        } else {
            return alignRight(sLine + getWhiteString((iSize - sLine.length()) / 2), iSize);
        }
    }

    /**
     *
     * @param sLine
     * @return Equalise Left/Right spacing
     */
    public static String alignCenter(String sLine) {
        return alignCenter(sLine, 42);
    }

    /**
     *
     * @param sCad
     * @return Convert number to string
     */
    public static byte[] transNumber(String sCad) {

        if (sCad == null) {
            return null;
        } else {
            byte bAux[] = new byte[sCad.length()];
            for (int i = 0; i < sCad.length(); i++) {
                bAux[i] = transNumberChar(sCad.charAt(i));
            }
            return bAux;
        }
    }

    /**
     *
     * @param sChar
     * @return Convert hex to character
     */
    public static byte transNumberChar(char sChar) {
        switch (sChar) {
            case '0':
                return 0x30;
            case '1':
                return 0x31;
            case '2':
                return 0x32;
            case '3':
                return 0x33;
            case '4':
                return 0x34;
            case '5':
                return 0x35;
            case '6':
                return 0x36;
            case '7':
                return 0x37;
            case '8':
                return 0x38;
            case '9':
                return 0x39;
            default:
                return 0x30;
        }
    }
}
