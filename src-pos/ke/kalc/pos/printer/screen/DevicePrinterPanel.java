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
package ke.kalc.pos.printer.screen;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import ke.kalc.globals.SystemProperty;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.AppProperties;
import ke.kalc.pos.forms.KALCFonts;
import ke.kalc.pos.printer.DevicePrinter;
import ke.kalc.pos.printer.ticket.BasicTicket;
import ke.kalc.pos.printer.ticket.BasicTicketForScreen;

/**
 *
 *
 */
public class DevicePrinterPanel extends javax.swing.JPanel implements DevicePrinter {

    private final String m_sName;
    private final JTicketContainer m_jTicketContainer;
    private BasicTicket m_ticketcurrent;
    private final AppProperties m_props;

    /**
     * Creates new form JPrinterScreen2
     *
     * @param props
     */
    public DevicePrinterPanel(AppProperties props) {
        initComponents();

        m_sName = AppLocal.getIntString("printer.screen");
        m_ticketcurrent = null;
        m_jTicketContainer = new JTicketContainer();
        m_jScrollView.setViewportView(m_jTicketContainer);
        m_props = props;
    }

    public DevicePrinterPanel(AppProperties props, Integer ptrIndex) {
        initComponents();

        switch (ptrIndex) {
            case 1:
                m_sName = AppLocal.getIntString("lbl.printerReceipt");
                break;
            case 2:
                m_sName = AppLocal.getIntString("lbl.printer1");
                break;
            case 3:
                m_sName = AppLocal.getIntString("lbl.printer2");
                break;
            case 4:
                m_sName = AppLocal.getIntString("lbl.printer3");
                break;
            case 5:
                m_sName = AppLocal.getIntString("lbl.printer4");
                break;
            case 6:
                m_sName = AppLocal.getIntString("lbl.printer5");
                break;
            case 7:
                m_sName = AppLocal.getIntString("lbl.printer6");
                break;
            case 8:
                m_sName = AppLocal.getIntString("lbl.printer7");
                break;
            case 9:
                m_sName = AppLocal.getIntString("lbl.printer8");
                break;
            case 10:
                m_sName = AppLocal.getIntString("lbl.printer9");
                break;
            case 11:
                m_sName = AppLocal.getIntString("lbl.printer10");
                break;
            default:
                m_sName = AppLocal.getIntString("printer.screen");
                break;

        }

        m_ticketcurrent = null;
        m_jTicketContainer = new JTicketContainer();
        m_jScrollView.setViewportView(m_jTicketContainer);
        m_props = props;
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
     */
    @Override
    public void printLogo(Byte iNumber) {
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
        return this;
    }

    /**
     *
     */
    @Override
    public void reset() {
        m_ticketcurrent = null;
        m_jTicketContainer.removeAllTickets();
        m_jTicketContainer.repaint();
    }

    // INTERFAZ PRINTER 2
    /**
     *
     */
    @Override
    public void beginReceipt() {
        m_ticketcurrent = new BasicTicketForScreen();

    }

    /**
     *
     * @param image
     */
    @Override
    public void printImage(BufferedImage image) {
        m_ticketcurrent.printImage(image);
    }

    /**
     *
     * @param type
     * @param position
     * @param code
     */
    @Override
    public Boolean printBarCode(String type, String position, String code) {
        m_ticketcurrent.printBarCode(type, position, code);
        return true;
    }

    /**
     *
     * @param iTextSize
     */
    @Override
    public void beginLine(int iTextSize) {
        m_ticketcurrent.beginLine(iTextSize);
    }

    /**
     *
     * @param iStyle
     * @param sText
     */
    @Override
    public void printText(int iStyle, String sText) {
        m_ticketcurrent.printText(iStyle, sText);
    }

    /**
     *
     */
    @Override
    public void endLine() {
        m_ticketcurrent.endLine();
    }

    /**
     *
     */
    @Override
    public void endReceipt() {
        m_jTicketContainer.addTicket(new JTicket(m_ticketcurrent, SystemProperty.SCRRECEIPTCOLS));
        m_ticketcurrent = null;
    }

    /**
     *
     */
    @Override
    public void openDrawer() {
        // Una simulacion
        Toolkit.getDefaultToolkit().beep();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jScrollView = new javax.swing.JScrollPane();

        setLayout(new java.awt.BorderLayout());

        m_jScrollView.setFont(KALCFonts.DEFAULTFONT.deriveFont(14f));
        add(m_jScrollView, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane m_jScrollView;
    // End of variables declaration//GEN-END:variables

}
