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
package ke.kalc.pos.sales;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import ke.kalc.basic.BasicException;
import ke.kalc.commons.dialogs.JAlertPane;
import ke.kalc.data.gui.JMessageDialog;
import ke.kalc.data.gui.ListKeyed;
import ke.kalc.data.gui.MessageInf;
import ke.kalc.format.Formats;
import ke.kalc.globals.CompanyInfo;
import ke.kalc.pos.datalogic.DataLogicCustomers;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.AppView;
import ke.kalc.pos.datalogic.DataLogicSales;
import ke.kalc.pos.datalogic.DataLogicSystem;
import ke.kalc.pos.forms.JPrincipalApp;
import ke.kalc.pos.forms.JRootApp;
import ke.kalc.pos.giftcards.GiftCardLogic;
import ke.kalc.pos.printer.DeviceTicket;
import ke.kalc.pos.printer.IncludeFile;
import ke.kalc.pos.printer.TicketParser;
import ke.kalc.pos.printer.TicketPrinterException;
import ke.kalc.pos.scripting.ScriptEngine;
import ke.kalc.pos.scripting.ScriptException;
import ke.kalc.pos.scripting.ScriptFactory;
import ke.kalc.pos.ticket.FindTicketsInfo;
import ke.kalc.pos.ticket.ReceiptTaxesInfo;
import ke.kalc.pos.ticket.TicketInfo;
import ke.kalc.pos.ticket.TicketLineInfo;
import ke.kalc.pos.ticket.TicketTaxInfo;
import ke.kalc.pos.ticket.TicketType;
import ke.kalc.globals.IconFactory;
import ke.kalc.pos.forms.AppUser;
import ke.kalc.pos.forms.KALCFonts;
import ke.kalc.pos.panels.JTicketsFinder;

public class JTicketsBagTicket extends JTicketsBag {

    private DataLogicSystem m_dlSystem = null;
    protected DataLogicCustomers dlCustomers = null;
    private final DataLogicSales m_dlSales;
    private TaxesLogic taxeslogic;
    private ListKeyed taxcollection;
    private final DeviceTicket m_TP;
    private final TicketParser m_TTP;
    private final TicketParser m_TTP2;
    private TicketInfo m_ticket;
    private TicketInfo m_ticketCopy;
    private final JTicketsBagTicketBag m_TicketsBagTicketBag;
    private final JPanelTicketEdits m_panelticketedit;

    private JPrincipalApp principalApp;

    private Boolean read = false;

    public JTicketsBagTicket(AppView app, JPanelTicketEdits panelticket) {

        super(app, panelticket);
        m_panelticketedit = panelticket;
        m_dlSystem = (DataLogicSystem) m_App.getBean("ke.kalc.pos.datalogic.DataLogicSystem");
        m_dlSales = (DataLogicSales) m_App.getBean("ke.kalc.pos.datalogic.DataLogicSales");
        dlCustomers = (DataLogicCustomers) m_App.getBean("ke.kalc.pos.datalogic.DataLogicCustomers");

        m_TP = new DeviceTicket(app.getProperties());
        m_TTP = new TicketParser(m_TP, m_dlSystem);
        m_TTP2 = new TicketParser(m_App.getDeviceTicket(), m_dlSystem);

        initComponents();
        m_TicketsBagTicketBag = new JTicketsBagTicketBag(this);
        m_jTicketEditor.addEditorKeys(m_jKeys);
        m_jPanelTicket.add(m_TP.getDevicePrinter("receiptprinter").getPrinterComponent(), BorderLayout.CENTER);

        try {
            taxeslogic = new TaxesLogic(m_dlSales.getTaxList(m_dlSales.getSiteGUID()).list());
        } catch (BasicException ex) {
        }
    }

    public void deleteAllShared() {
    }

    @Override
    public void activate() {
        principalApp = JRootApp.getPricipalApp();

        m_ticket = null;
        m_ticketCopy = null;

        printTicket();

        m_jTicketEditor.reset();
        m_jTicketEditor.activate();

        m_panelticketedit.setActiveTicket(null, null);

        jrbSales.setSelected(true);
        m_jRefund.setVisible(AppUser.hasPermission("sales.RefundTicket"));
        m_jPrint.setVisible(AppUser.hasPermission("sales.PrintTicket"));

    }

    @Override
    public boolean deactivate() {

        m_ticket = null;
        m_ticketCopy = null;
        return true;
    }

    @Override
    public void deleteTicket() {
        if (m_ticketCopy != null) {
            try {
                m_dlSales.deleteTicket(m_ticketCopy, m_App.getInventoryLocation());
            } catch (BasicException eData) {
                MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.nosaveticket"), eData);
                msg.show(this);
            }
        }

        m_ticket = null;
        m_ticketCopy = null;
        resetToTicket();
    }

    public void canceleditionTicket() {
        m_ticketCopy = null;
        resetToTicket();
    }

    private void resetToTicket() {
        printTicket();
        m_jTicketEditor.reset();
        m_jTicketEditor.activate();
        m_panelticketedit.setActiveTicket(null, null);
    }

    @Override
    protected JComponent getBagComponent() {
        return m_TicketsBagTicketBag;
    }

    @Override
    protected JComponent getNullComponent() {
        return this;
    }

    private void readTicket(int iTicketid, int iTickettype) {
        Integer findTicket = 0;

        if (m_jKeys.getVoucher().startsWith("P")) {
            iTicketid = m_dlSystem.getTicketId(m_jKeys.getVoucher().substring(1));
            iTickettype = m_dlSystem.getTicketType(m_jKeys.getVoucher().substring(1));
        }

        try {
            findTicket = m_jTicketEditor.getValueInteger();
        } catch (BasicException e) {
        }
        try {
            TicketInfo ticket = (iTicketid == -1)
                    ? m_dlSales.loadTicket(iTickettype, findTicket)
                    : m_dlSales.loadTicket(iTickettype, iTicketid);
            if (ticket == null) {
                JAlertPane.messageBox(JAlertPane.WARNING, AppLocal.getIntString("message.notexiststicket"), 16, new Dimension(125, 50), JAlertPane.OK_OPTION);
            } else {
                m_ticket = ticket;
                m_ticketCopy = null;

                try {
                    taxeslogic.calculateTaxes(m_ticket);
                    TicketTaxInfo[] taxlist = m_ticket.getTaxLines();
                } catch (TaxesException ex) {
                }
                printTicket();
            }

        } catch (BasicException e) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotloadticket"), e);
            msg.show(this);
        }
        m_jTicketEditor.reset();
        m_jTicketEditor.activate();
    }

    private void printTicket() {
        //Print the ticket in the sales panel
        m_jRefund.setEnabled(m_ticket != null && (m_ticket.isNormal() || m_ticket.isInvoice()));
        m_jPrint.setEnabled(m_ticket != null);

        m_TP.getDevicePrinter("receiptprinter").reset();

        String source = m_dlSystem.getResourceAsXML("Printer.TicketRefund");
        String sresource;
        IncludeFile incFile = new IncludeFile(source, m_dlSystem);

        if (m_ticket == null) {
            m_jTicketId.setText(null);
        } else {

            m_jTicketId.setText(m_ticket.getName());
            sresource = incFile.processInclude();
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                script.put("salesticket", m_ticket.getLine(0).getProperty("salesticket"));
                script.put("ticket", m_ticket);
                script.put("pickupcode", "P" + m_ticket.getId().substring(24));
                script.put("taxincluded", m_ticket.isTaxInclusive());
                script.put("company", new CompanyInfo());
                script.put("giftcard", new GiftCardLogic());
                ReceiptTaxesInfo ltr = new ReceiptTaxesInfo();

                try {
                    List<ReceiptTaxesInfo> lines = ltr.getReceiptTaxLines(m_dlSales.getLineTaxRates(m_ticket.getId()));
                    script.put("nett", ((m_ticket.isTaxInclusive()) ? ltr.printReceiptSubTotal(lines, m_ticket.getTicketTotal()) : Formats.CURRENCY.formatValue(m_ticket.getSubTotalExcluding())));
                    script.put("tickettaxdetails", lines);
                } catch (BasicException ex) {
                    Logger.getLogger(JTicketsBagTicket.class.getName()).log(Level.SEVERE, null, ex);
                }

                m_TTP.printTicket(script.eval(sresource).toString());

            } catch (ScriptException | TicketPrinterException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
                msg.show(this);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        m_jOptions = new javax.swing.JPanel();
        m_jButtons = new javax.swing.JPanel();
        m_jTicketId = new javax.swing.JLabel();
        m_jPrint = new javax.swing.JButton();
        m_jRefund = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        btnActionMenu = new javax.swing.JButton();
        jBtnSales = new javax.swing.JButton();
        m_jPanelTicket = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        m_jKeys = new ke.kalc.editor.JEditorKeys();
        jPanel5 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        m_jTicketEditor = new ke.kalc.editor.JEditorIntegerPositive();
        jPanel1 = new javax.swing.JPanel();
        jrbSales = new javax.swing.JRadioButton();
        jrbRefunds = new javax.swing.JRadioButton();

        setLayout(new java.awt.BorderLayout());

        m_jButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        m_jTicketId.setFont(KALCFonts.DEFAULTFONTBOLD);
        m_jTicketId.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_jTicketId.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jTicketId.setOpaque(true);
        m_jTicketId.setPreferredSize(new java.awt.Dimension(250, 35));
        m_jTicketId.setVerifyInputWhenFocusTarget(false);
        m_jButtons.add(m_jTicketId);

        m_jPrint.setFont(KALCFonts.DEFAULTBUTTONFONT);
        m_jPrint.setIcon(IconFactory.getIcon("printer24.png"));
        m_jPrint.setFocusPainted(false);
        m_jPrint.setFocusable(false);
        m_jPrint.setMargin(new java.awt.Insets(0, 4, 0, 4));
        m_jPrint.setMaximumSize(new java.awt.Dimension(50, 40));
        m_jPrint.setMinimumSize(new java.awt.Dimension(50, 40));
        m_jPrint.setPreferredSize(new java.awt.Dimension(50, 40));
        m_jPrint.setRequestFocusEnabled(false);
        m_jPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jPrintActionPerformed(evt);
            }
        });
        m_jButtons.add(m_jPrint);

        m_jRefund.setFont(KALCFonts.DEFAULTBUTTONFONT);
        m_jRefund.setIcon(IconFactory.getIcon("notes.png"));
        m_jRefund.setFocusPainted(false);
        m_jRefund.setFocusable(false);
        m_jRefund.setMargin(new java.awt.Insets(0, 4, 0, 4));
        m_jRefund.setMaximumSize(new java.awt.Dimension(50, 40));
        m_jRefund.setMinimumSize(new java.awt.Dimension(50, 40));
        m_jRefund.setPreferredSize(new java.awt.Dimension(50, 40));
        m_jRefund.setRequestFocusEnabled(false);
        m_jRefund.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jRefundActionPerformed(evt);
            }
        });
        m_jButtons.add(m_jRefund);

        btnSearch.setFont(KALCFonts.DEFAULTBUTTONFONT);
        btnSearch.setIcon(IconFactory.getIcon("search24.png"));
        btnSearch.setFocusPainted(false);
        btnSearch.setFocusable(false);
        btnSearch.setMargin(new java.awt.Insets(0, 4, 0, 4));
        btnSearch.setMaximumSize(new java.awt.Dimension(50, 40));
        btnSearch.setMinimumSize(new java.awt.Dimension(50, 40));
        btnSearch.setPreferredSize(new java.awt.Dimension(50, 40));
        btnSearch.setRequestFocusEnabled(false);
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        m_jButtons.add(btnSearch);
        m_jButtons.add(jPanel2);

        btnActionMenu.setFont(KALCFonts.DEFAULTBUTTONFONT);
        btnActionMenu.setIcon(IconFactory.getIcon("menu.png"));
        btnActionMenu.setFocusPainted(false);
        btnActionMenu.setFocusable(false);
        btnActionMenu.setMargin(new java.awt.Insets(0, 4, 0, 4));
        btnActionMenu.setMaximumSize(new java.awt.Dimension(52, 40));
        btnActionMenu.setMinimumSize(new java.awt.Dimension(52, 40));
        btnActionMenu.setPreferredSize(new java.awt.Dimension(40, 40));
        btnActionMenu.setRequestFocusEnabled(false);
        btnActionMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenu(evt);
            }
        });

        jBtnSales.setFont(KALCFonts.DEFAULTBUTTONFONT);
        jBtnSales.setIcon(IconFactory.getIcon("sale_new.png"));
        jBtnSales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnSalesActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout m_jOptionsLayout = new org.jdesktop.layout.GroupLayout(m_jOptions);
        m_jOptions.setLayout(m_jOptionsLayout);
        m_jOptionsLayout.setHorizontalGroup(
            m_jOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(m_jOptionsLayout.createSequentialGroup()
                .add(btnActionMenu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jBtnSales, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(95, 95, 95)
                .add(m_jButtons, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE))
        );
        m_jOptionsLayout.setVerticalGroup(
            m_jOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(m_jOptionsLayout.createSequentialGroup()
                .add(0, 0, 0)
                .add(m_jOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(m_jButtons, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(m_jOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jBtnSales, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, btnActionMenu, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .add(12, 12, 12))
        );

        add(m_jOptions, java.awt.BorderLayout.NORTH);

        m_jPanelTicket.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        m_jPanelTicket.setFont(KALCFonts.DEFAULTFONT.deriveFont(14f)
        );
        m_jPanelTicket.setLayout(new java.awt.BorderLayout());
        add(m_jPanelTicket, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.Y_AXIS));

        m_jKeys.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jKeysActionPerformed(evt);
            }
        });
        jPanel4.add(m_jKeys);

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel5.setLayout(new java.awt.GridBagLayout());

        jButton1.setIcon(IconFactory.getIcon("ok.png"));
        jButton1.setFocusPainted(false);
        jButton1.setFocusable(false);
        jButton1.setMargin(new java.awt.Insets(8, 14, 8, 14));
        jButton1.setRequestFocusEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel5.add(jButton1, gridBagConstraints);

        m_jTicketEditor.setFont(KALCFonts.DEFAULTFONT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel5.add(m_jTicketEditor, gridBagConstraints);

        jPanel4.add(jPanel5);

        jPanel3.add(jPanel4, java.awt.BorderLayout.NORTH);

        buttonGroup1.add(jrbSales);
        jrbSales.setFont(KALCFonts.DEFAULTFONTBOLD);
        jrbSales.setText(AppLocal.getIntString("label.sales")); // NOI18N
        jrbSales.setFocusPainted(false);
        jrbSales.setFocusable(false);
        jrbSales.setRequestFocusEnabled(false);
        jPanel1.add(jrbSales);

        buttonGroup1.add(jrbRefunds);
        jrbRefunds.setFont(KALCFonts.DEFAULTFONTBOLD);
        jrbRefunds.setForeground(new java.awt.Color(255, 0, 0));
        jrbRefunds.setText(AppLocal.getIntString("label.refunds")); // NOI18N
        jrbRefunds.setFocusPainted(false);
        jrbRefunds.setFocusable(false);
        jrbRefunds.setRequestFocusEnabled(false);
        jPanel1.add(jrbRefunds);

        jPanel3.add(jPanel1, java.awt.BorderLayout.CENTER);

        add(jPanel3, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jPrintActionPerformed
        if (m_ticket != null) {
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                script.put("salesticket", m_ticket.getLine(0).getProperty("salesticket"));
                script.put("ticket", m_ticket);
                script.put("pickupcode", "P" + m_ticket.getId().substring(24));
                script.put("taxincluded", m_ticket.isTaxInclusive());
                script.put("company", new CompanyInfo());
                script.put("giftcard", new GiftCardLogic());
                ReceiptTaxesInfo ltr = new ReceiptTaxesInfo();

                try {
                    List<ReceiptTaxesInfo> lines = ltr.getReceiptTaxLines(m_dlSales.getLineTaxRates(m_ticket.getId()));
                    script.put("nett", ((m_ticket.isTaxInclusive()) ? ltr.printReceiptSubTotal(lines, m_ticket.getTicketTotal()) : Formats.CURRENCY.formatValue(m_ticket.getSubTotalExcluding())));
                    script.put("tickettaxdetails", lines);
                } catch (BasicException ex) {
                    Logger.getLogger(JTicketsBagTicket.class.getName()).log(Level.SEVERE, null, ex);
                }
                String sresource;
                String source;

                IncludeFile incFile;
                switch (m_ticket.getTicketType()) {
                    case NORMAL:
                    case REFUND:
                        source = m_dlSystem.getResourceAsXML("Printer.TicketRefund");
                        incFile = new IncludeFile(source, m_dlSystem);
                        m_jTicketId.setText(m_ticket.getName());
                        sresource = incFile.processInclude();
                        m_TTP2.printTicket(script.eval(sresource).toString());
                        break;
                    case INVOICE:
                        source = m_dlSystem.getResourceAsXML("Display.Ticket2");
                        incFile = new IncludeFile(source, m_dlSystem);
                        sresource = incFile.processInclude();
                        m_TTP2.printTicket(script.eval(sresource).toString());
                        //   m_TTP2.printTicket(script.eval(m_dlSystem.getResourceAsXML("Display.Ticket2")).toString());
                        break;
                    case NOSALE:
                    default:
                        source = m_dlSystem.getResourceAsXML("Printer.TicketPreview");
                        incFile = new IncludeFile(source, m_dlSystem);
                        sresource = incFile.processInclude();
                        m_TTP2.printTicket(script.eval(sresource).toString());
                        //  m_TTP2.printTicket(script.eval(m_dlSystem.getResourceAsXML("Printer.TicketPreview")).toString());
                        break;
                }
            } catch (ScriptException e) {
                JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotprint"), e));
            } catch (TicketPrinterException e) {
                JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotprint"), e));
            }
        }
    }//GEN-LAST:event_m_jPrintActionPerformed

    private void m_jRefundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jRefundActionPerformed
        java.util.List aRefundLines = new ArrayList();
        Boolean sCharge = false;
        for (int i = 0; i < m_ticket.getLinesCount(); i++) {
            TicketLineInfo newline = new TicketLineInfo(m_ticket.getLine(i), false);
            if (newline.getProductID().equalsIgnoreCase("servicecharge")) {
                sCharge = true;
            }
            newline.setRefundTicket(m_ticket.getLine(i).getTicket(), m_ticket.getLine(i).getTicketLine());
            newline.setMultiply(newline.getMultiply() - m_ticket.getLine(i).getRefundQty());
            newline.setOrderQty(newline.getMultiply() + m_ticket.getLine(i).getRefundQty());
            if ((newline.getMultiply()) > 0) {
                aRefundLines.add(newline);
            }
        }

        if (aRefundLines.isEmpty() || (aRefundLines.size() == 1 & sCharge)) {
            Toolkit.getDefaultToolkit().beep();
            JAlertPane.messageBox(JAlertPane.WARNING, AppLocal.getIntString("message.refundable"), 16, new Dimension(125, 50), JAlertPane.OK_OPTION);
        } else {
            m_ticketCopy = null;
            m_TicketsBagTicketBag.showRefund();
            m_panelticketedit.showRefundLines(aRefundLines);
            TicketInfo refundticket = new TicketInfo();
            refundticket.setTicketType(TicketType.REFUND);
            refundticket.setCustomer(m_ticket.getCustomer());
            refundticket.setPayments(m_ticket.getPayments());
            refundticket.setOldTicket(true);
            refundticket.setProperty("oldticket", m_ticket.getId());
            m_panelticketedit.setActiveTicket(refundticket, null);
        }

    }//GEN-LAST:event_m_jRefundActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        readTicket(-1, jrbSales.isSelected() ? 0 : 1);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void m_jKeysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jKeysActionPerformed
        readTicket(-1, jrbSales.isSelected() ? 0 : 1);
    }//GEN-LAST:event_m_jKeysActionPerformed

private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
    JTicketsFinder finder = new JTicketsFinder(m_dlSales, dlCustomers);
    finder.setVisible(true);

    FindTicketsInfo selectedTicket = finder.getSelectedTicket();
    if (selectedTicket == null) {
        m_jTicketEditor.reset();
        m_jTicketEditor.activate();
    } else {
        readTicket(selectedTicket.getTicketId(), selectedTicket.getTicketType());
    }
}//GEN-LAST:event_btnSearchActionPerformed

    private void btnMenu(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenu
        principalApp.setMenuVisible(btnActionMenu);
    }//GEN-LAST:event_btnMenu

    private void jBtnSalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnSalesActionPerformed
        principalApp.showTask("ke.kalc.pos.sales.JPanelTicketSales");
    }//GEN-LAST:event_jBtnSalesActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActionMenu;
    private javax.swing.JButton btnSearch;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jBtnSales;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JRadioButton jrbRefunds;
    private javax.swing.JRadioButton jrbSales;
    private javax.swing.JPanel m_jButtons;
    private ke.kalc.editor.JEditorKeys m_jKeys;
    private javax.swing.JPanel m_jOptions;
    private javax.swing.JPanel m_jPanelTicket;
    private javax.swing.JButton m_jPrint;
    private javax.swing.JButton m_jRefund;
    private ke.kalc.editor.JEditorIntegerPositive m_jTicketEditor;
    private javax.swing.JLabel m_jTicketId;
    // End of variables declaration//GEN-END:variables

    @Override
    public void getTicketByCode(String id) {

    }

}
