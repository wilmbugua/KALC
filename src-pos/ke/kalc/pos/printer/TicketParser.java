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

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ke.kalc.basic.BasicException;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.datalogic.DataLogicSystem;
import ke.kalc.pos.qrcode.BarCode;
import ke.kalc.pos.ticket.TicketInfo;
import java.util.logging.Logger;
import ke.kalc.pos.forms.AppUser;
import ke.kalc.pos.ticket.TicketType;

/**
 *
 *
 */
public class TicketParser extends DefaultHandler {

    private static SAXParser m_sp = null;

    private static final Logger logger = Logger.getLogger(TicketParser.class.getName());

    private DeviceTicket m_printer;
    private DataLogicSystem m_system;

    private StringBuilder text;

    private String bctype;
    private String bcposition;
    private int bcwidth, bcheight, qrsize;
    private int m_iTextAlign;
    private int m_iTextLength;
    private int m_iTextStyle;
    private int size;

    private StringBuilder m_sVisorLine;
    private int m_iVisorAnimation;
    private String m_sVisorLine1;
    private String m_sVisorLine2;

    private double m_dValue1;
    private double m_dValue2;
    private int attribute3;

    private int m_iOutputType;
    private static final int OUTPUT_NONE = 0;
    private static final int OUTPUT_DISPLAY = 1;
    private static final int OUTPUT_TICKET = 2;
    private static final int OUTPUT_FISCAL = 3;
    private DevicePrinter m_oOutputPrinter;
    private DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private Date today;
    private String cUser;
    private String ticketId;
    private String pickupId;

    /**
     * Creates a new instance of TicketParser
     *
     * @param printer
     * @param system
     */
    public TicketParser(DeviceTicket printer, DataLogicSystem system) {
        m_printer = printer;
        m_system = system;
        today = Calendar.getInstance().getTime();
    }

    /**
     *
     * @param sIn
     * @param ticket
     * @throws TicketPrinterException
     */
    public void printTicket(String sIn, TicketInfo ticket) throws TicketPrinterException {
        // cUser = ticket.getName();
        if (ticket.getUser() != null) {
            cUser = ticket.getUser().getId();
        }
        ticketId = Integer.toString(ticket.getTicketId());
        pickupId = Integer.toString(ticket.getPickupId());

        if (ticket.getTicketId() == 0) {
            ticketId = "No Sale";
        }
        if (ticket.getPickupId() == 0) {
            pickupId = "No PickupId";
        }
        printTicket(new StringReader(sIn));

    }

    /**
     *
     * @param sIn
     * @throws TicketPrinterException
     */
    public void printTicket(String sIn) throws TicketPrinterException {
        printTicket(new StringReader(sIn));
    }

    /**
     *
     * @param in
     * @throws TicketPrinterException
     */
    public void printTicket(Reader in) throws TicketPrinterException {
        try {
            if (m_sp == null) {
                SAXParserFactory spf = SAXParserFactory.newInstance();
                m_sp = spf.newSAXParser();
            }
            m_sp.parse(new InputSource(in), this);

        } catch (ParserConfigurationException ePC) {
            throw new TicketPrinterException(AppLocal.getIntString("exception.parserconfig"), ePC);
        } catch (SAXException eSAX) {
            throw new TicketPrinterException(AppLocal.getIntString("exception.xmlfile"), eSAX);
        } catch (IOException eIO) {
            throw new TicketPrinterException(AppLocal.getIntString("exception.iofile"), eIO);
        }
    }

    @Override
    public void startDocument() throws SAXException {
        text = null;
        bctype = null;
        bcposition = null;
        m_sVisorLine = null;
        m_iVisorAnimation = DeviceDisplayBase.ANIMATION_NULL;
        m_sVisorLine1 = null;
        m_sVisorLine2 = null;
        m_iOutputType = OUTPUT_NONE;
        m_oOutputPrinter = null;
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        String openDate = df.format(today);
        Date dNow = new Date();

        switch (m_iOutputType) {
            case OUTPUT_NONE:
                switch (qName) {
                    case "opendrawer":
                        m_printer.getDevicePrinter(readString(attributes.getValue("printer"), "receiptprinter")).openDrawer();
                        try {
                            m_system.execDrawerOpened(new Object[]{UUID.randomUUID().toString(), cUser, ticketId});
                        } catch (BasicException ex) {
                        }
                        break;
                    case "opendrawerNoLog":
                        m_printer.getDevicePrinter(readString(attributes.getValue("printer"), "receiptprinter")).openDrawer();
                        break;
                    case "play":
                        text = new StringBuilder();
                        break;
                    case "ticket":
                        m_iOutputType = OUTPUT_TICKET;
                        m_oOutputPrinter = m_printer.getDevicePrinter(readString(attributes.getValue("printer"), "receiptprinter"));
                        m_oOutputPrinter.beginReceipt();
                        break;
                    case "display":
                        m_iOutputType = OUTPUT_DISPLAY;
                        String animation = attributes.getValue("animation");
                        if ("scroll".equals(animation)) {
                            m_iVisorAnimation = DeviceDisplayBase.ANIMATION_SCROLL;
                        } else if ("flyer".equals(animation)) {
                            m_iVisorAnimation = DeviceDisplayBase.ANIMATION_FLYER;
                        } else if ("blink".equals(animation)) {
                            m_iVisorAnimation = DeviceDisplayBase.ANIMATION_BLINK;
                        } else if ("curtain".equals(animation)) {
                            m_iVisorAnimation = DeviceDisplayBase.ANIMATION_CURTAIN;
                        } else { // "none"
                            m_iVisorAnimation = DeviceDisplayBase.ANIMATION_NULL;
                        }
                        m_sVisorLine1 = null;
                        m_sVisorLine2 = null;
                        m_oOutputPrinter = null;
                        break;
                    case "fiscalreceipt":
                        m_iOutputType = OUTPUT_FISCAL;
                        m_printer.getFiscalPrinter().beginReceipt();
                        break;
                    case "fiscalzreport":
                        m_printer.getFiscalPrinter().printZReport();
                        break;
                    case "fiscalxreport":
                        m_printer.getFiscalPrinter().printXReport();
                        break;
                }
                break;
            case OUTPUT_TICKET:
                if (qName != null) {
                    switch (qName) {
                        case "logo":
                            text = new StringBuilder();
                            break;
                        case "image":
                            text = new StringBuilder();
                            break;
                        case "qrcode":
                            text = new StringBuilder();
                            qrsize = parseInt(attributes.getValue("size"));
                            break;
                        case "qrcode64":
                            text = new StringBuilder();
                            qrsize = parseInt(attributes.getValue("size"));
                            break;
                        case "barcode":
                            text = new StringBuilder();
                            bctype = attributes.getValue("type");
                            bcposition = attributes.getValue("position");
                            break;
                        case "prtbarcode":
                            text = new StringBuilder();
                            bctype = attributes.getValue("type");
                            bcwidth = parseInt(attributes.getValue("width"));
                            bcheight = parseInt(attributes.getValue("height"));
                            break;
                        case "type":
                            text = new StringBuilder();
                            bctype = attributes.getValue("type");
                            size = parseInt(attributes.getValue("size"));
                            break;
                        case "line":
                            m_oOutputPrinter.beginLine(parseInt(attributes.getValue("size"), DevicePrinter.SIZE_0));
                            break;
                        case "text":
                            text = new StringBuilder();
                            m_iTextStyle = ("true".equals(attributes.getValue("bold")) ? DevicePrinter.STYLE_BOLD : DevicePrinter.STYLE_PLAIN)
                                    | ("true".equals(attributes.getValue("underline")) ? DevicePrinter.STYLE_UNDERLINE : DevicePrinter.STYLE_PLAIN);
                            String sAlign = attributes.getValue("align");
                            if ("right".equals(sAlign)) {
                                m_iTextAlign = DevicePrinter.ALIGN_RIGHT;
                            } else if ("center".equals(sAlign)) {
                                m_iTextAlign = DevicePrinter.ALIGN_CENTER;
                            } else {
                                m_iTextAlign = DevicePrinter.ALIGN_LEFT;
                            }
                            m_iTextLength = parseInt(attributes.getValue("length"), 0);
                            break;
                    }
                }
                break;
            case OUTPUT_DISPLAY:
                if (null != qName) {
                    switch (qName) {
                        case "line":
                            m_sVisorLine = new StringBuilder();
                            break;
                        case "line1":
                            m_sVisorLine = new StringBuilder();
                            break;
                        case "line2":
                            m_sVisorLine = new StringBuilder();
                            break;
                        case "text":
                            text = new StringBuilder();
                            String sAlign = attributes.getValue("align");
                            if ("right".equals(sAlign)) {
                                m_iTextAlign = DevicePrinter.ALIGN_RIGHT;
                            } else if ("center".equals(sAlign)) {
                                m_iTextAlign = DevicePrinter.ALIGN_CENTER;
                            } else {
                                m_iTextAlign = DevicePrinter.ALIGN_LEFT;
                            }
                            m_iTextLength = parseInt(attributes.getValue("length"));
                            break;
                    }
                }
                break;
            case OUTPUT_FISCAL:
                if (null != qName) {
                    switch (qName) {
                        case "line":
                            text = new StringBuilder();
                            m_dValue1 = parseDouble(attributes.getValue("price"));
                            m_dValue2 = parseDouble(attributes.getValue("units"), 1.0);
                            attribute3 = parseInt(attributes.getValue("tax"));
                            break;
                        case "message":
                            text = new StringBuilder();
                            break;
                        case "total":
                            text = new StringBuilder();
                            m_dValue1 = parseDouble(attributes.getValue("paid"));
                            break;
                        default:
                            break;
                    }
                }
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        switch (m_iOutputType) {
            case OUTPUT_NONE:
                if ("play".equals(qName)) {
                    try {
                        AudioClip oAudio = Applet.newAudioClip(getClass().getClassLoader().getResource(text.toString()));
                        oAudio.play();
                    } catch (Exception fnfe) {
                        //throw new ResourceNotFoundException( fnfe.getMessage() );
                    }
                    text = null;
                }
                break;

// Added 23.05.13 used by star TSP700 to print stored logo image JDL            
            case OUTPUT_TICKET:
                if (null != qName) {
                    switch (qName) {
                        case "logo":
                            Byte firstLogo = 0x01;
                            if (text.toString().isEmpty()) {
                                m_oOutputPrinter.printLogo(firstLogo);
                            } else if (((Integer.parseInt(text.toString())) > 1) && ((Integer.parseInt(text.toString())) < 256)) {
                                m_oOutputPrinter.printLogo(Byte.parseByte(text.toString()));
                            } else {
                                m_oOutputPrinter.printLogo(firstLogo);
                            }
                        case "image":
                            try {
                            BufferedImage image = m_system.getResourceAsImage(text.toString());
                            if (image != null) {
                                m_oOutputPrinter.printImage(image);
                            }
                        } catch (Exception fnfe) {
                            //throw new ResourceNotFoundException( fnfe.getMessage() );
                        }
                        text = null;
                        break;
                        case "barcode":
                            if (!m_oOutputPrinter.printBarCode(bctype, bcposition, text.toString())) {
                                try {
                                    BarCode barCode = new BarCode();
                                    BufferedImage image;

                                    image = barCode.getBarcode(text.toString(), bctype, 0, 20);
                                    if (image != null) {
                                        m_oOutputPrinter.printImage(image);
                                    }
                                } catch (Exception fnfe) {
                                }
                            }
                            text = null;
                            break;
                        case "qrcode":
                    try {
                            BarCode qrCode = new BarCode();
                            BufferedImage image;
                            if (qrsize < 100) {
                                image = qrCode.getQRCode(text.toString(), 100);
                            } else {
                                image = qrCode.getQRCode(text.toString(), qrsize);
                            }
                            if (image != null) {
                                m_oOutputPrinter.printImage(image);
                            }
                        } catch (Exception fnfe) {
                        }
                        text = null;
                        break;
                        case "qrcode64":
                    try {
                            BarCode qrCode = new BarCode();
                            BufferedImage image;
                            if (qrsize < 100) {
                                image = qrCode.getQRCodeBase64(text.toString(), 100);
                            } else {
                                image = qrCode.getQRCodeBase64(text.toString(), qrsize);
                            }
                            if (image != null) {
                                m_oOutputPrinter.printImage(image);
                            }
                        } catch (Exception fnfe) {
                        }
                        text = null;
                        break;
                        case "text":
                            if (m_iTextLength > 0) {
                                switch (m_iTextAlign) {
                                    case DevicePrinter.ALIGN_RIGHT:
                                        m_oOutputPrinter.printText(m_iTextStyle, DeviceTicket.alignRight(text.toString(), m_iTextLength));
                                        break;
                                    case DevicePrinter.ALIGN_CENTER:
                                        m_oOutputPrinter.printText(m_iTextStyle, DeviceTicket.alignCenter(text.toString(), m_iTextLength));
                                        break;
                                    default: // DevicePrinter.ALIGN_LEFT
                                        m_oOutputPrinter.printText(m_iTextStyle, DeviceTicket.alignLeft(text.toString(), m_iTextLength));
                                        break;
                                }
                            } else {
                                m_oOutputPrinter.printText(m_iTextStyle, text.toString());
                            }
                            text = null;
                            break;
                        case "line":
                            m_oOutputPrinter.endLine();
                            break;
                        case "ticket":
                            m_oOutputPrinter.endReceipt();
                            m_iOutputType = OUTPUT_NONE;
                            m_oOutputPrinter = null;
                            break;
                    }
                }
                break;
            case OUTPUT_DISPLAY:
                if (null != qName) {
                    switch (qName) {
                        case "line":
                            if (m_sVisorLine1 == null) {
                                m_sVisorLine1 = m_sVisorLine.toString();
                            } else {
                                m_sVisorLine2 = m_sVisorLine.toString();
                            }
                            m_sVisorLine = null;
                            break;
                        case "line1":
                            m_sVisorLine1 = m_sVisorLine.toString();
                            m_sVisorLine = null;
                            break;
                        case "line2":
                            m_sVisorLine2 = m_sVisorLine.toString();
                            m_sVisorLine = null;
                            break;
                        case "text":
                            if (m_iTextLength > 0) {
                                switch (m_iTextAlign) {
                                    case DevicePrinter.ALIGN_RIGHT:
                                        m_sVisorLine.append(DeviceTicket.alignRight(text.toString(), m_iTextLength));
                                        break;
                                    case DevicePrinter.ALIGN_CENTER:
                                        m_sVisorLine.append(DeviceTicket.alignCenter(text.toString(), m_iTextLength));
                                        break;
                                    default: // DevicePrinter.ALIGN_LEFT
                                        m_sVisorLine.append(DeviceTicket.alignLeft(text.toString(), m_iTextLength));
                                        break;
                                }
                            } else {
                                m_sVisorLine.append(text);
                            }
                            text = null;
                            break;
                        case "display":
                            m_printer.getDeviceDisplay().writeVisor(m_iVisorAnimation, m_sVisorLine1, m_sVisorLine2);
                            m_iVisorAnimation = DeviceDisplayBase.ANIMATION_NULL;
                            m_sVisorLine1 = null;
                            m_sVisorLine2 = null;
                            m_iOutputType = OUTPUT_NONE;
                            m_oOutputPrinter = null;
                            break;
                    }
                }
                break;
            case OUTPUT_FISCAL:
                if (qName != null) {
                    switch (qName) {
                        case "fiscalreceipt":
                            m_printer.getFiscalPrinter().endReceipt();
                            m_iOutputType = OUTPUT_NONE;
                            break;
                        case "line":
                            m_printer.getFiscalPrinter().printLine(text.toString(), m_dValue1, m_dValue2, attribute3);
                            text = null;
                            break;
                        case "message":
                            m_printer.getFiscalPrinter().printMessage(text.toString());
                            text = null;
                            break;
                        case "total":
                            m_printer.getFiscalPrinter().printTotal(text.toString(), m_dValue1);
                            text = null;
                            break;
                    }
                }
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (text != null) {
            text.append(ch, start, length);
        }
    }

    private int parseInt(String sValue, int iDefault) {
        try {
            return Integer.parseInt(sValue);
        } catch (NumberFormatException eNF) {
            return iDefault;
        }
    }

    private int parseInt(String sValue) {
        return parseInt(sValue, 0);
    }

    private double parseDouble(String sValue, double ddefault) {
        try {
            return Double.parseDouble(sValue);
        } catch (NumberFormatException eNF) {
            return ddefault;
        }
    }

    private double parseDouble(String sValue) {
        return parseDouble(sValue, 0.0);
    }

    private String readString(String sValue, String sDefault) {
        if (sValue == null || sValue.equals("")) {
            return sDefault;
        } else {
            return sValue;
        }
    }

    /**
     * Prints a ticket with user permission validation.
     *
     * @param ticket the ticket to print
     * @param user the user attempting to print
     * @throws SecurityException if the user lacks permission
     */
    public void printTicket(TicketInfo ticket, AppUser user) throws SecurityException {
        // Validate user permissions
        if (!AppUser.hasPermission("receipts.print")) {
            logger.warning("User does not have print permission: " + user.getId());
            throw new SecurityException("Insufficient permissions to print tickets.");
        }

        // Validate ticket parameters
        if (ticket == null || ticket.getId() == null || ticket.getTicketId() <= 0) {
            logger.warning("Invalid ticket input parameters.");
            throw new IllegalArgumentException("Invalid ticket data.");
        }

        // Log the print operation
        logger.info("User " + user.getId() + " is printing ticket ID: " + ticket.getTicketId());
        performPrint(ticket);
    }

    private void performPrint(TicketInfo ticket) {
        try {
            String resource;
            TicketType type = ticket.getTicketType();
            if (type == TicketType.REFUND) {
                resource = "Printer.TicketRefund";
            } else if (type == TicketType.INVOICE) {
                resource = "Printer.TicketInvoice";
            } else {
                resource = "Printer.Ticket";
            }
            String sIn = m_system.getResourceAsXML(resource);
            printTicket(sIn, ticket);
        } catch (TicketPrinterException e) {
            throw new RuntimeException("Failed to print ticket", e);
        }
    }

}
