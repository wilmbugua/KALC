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
package uk.kalc.pos.panels;

import java.awt.Dimension;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import uk.kalc.basic.BasicException;
import uk.kalc.commons.dialogs.JAlertPane;
import uk.kalc.data.gui.MessageInf;
import uk.kalc.data.gui.TableRendererBasic;
import uk.kalc.data.loader.Datas;
import uk.kalc.data.loader.SerializerWriteBasic;
import uk.kalc.data.loader.StaticSentence;
import uk.kalc.format.Formats;
import uk.kalc.globals.CompanyInfo;
import uk.kalc.pos.forms.AppLocal;
import uk.kalc.pos.forms.AppView;
import uk.kalc.pos.forms.BeanFactoryApp;
import uk.kalc.pos.forms.BeanFactoryException;
import uk.kalc.pos.forms.JPanelView;
import uk.kalc.pos.printer.IncludeFile;
import uk.kalc.pos.printer.TicketParser;
import uk.kalc.pos.printer.TicketPrinterException;
import uk.kalc.pos.scripting.ScriptEngine;
import uk.kalc.pos.scripting.ScriptException;
import uk.kalc.pos.scripting.ScriptFactory;
import uk.kalc.globals.IconFactory;
import uk.kalc.globals.SystemProperty;
import uk.kalc.pos.datalogic.DataLogicSystem;
import uk.kalc.pos.forms.KALCFonts;

public class JPanelCloseMoney extends JPanel implements JPanelView, BeanFactoryApp {

    private AppView m_App;

    private DataLogicSystem dbQuery;
    private Integer result;
    private List<HourlySalesInfo> hSales;
    private List<UserSales> userSales;
    private PaymentsModel m_PaymentsToClose = null;
    private TicketParser m_TTP;
    private Boolean reload = false;

    /**
     * Creates new form JPanelCloseMoney
     */
    public JPanelCloseMoney() {
        initComponents();

    }

    /**
     *
     * @param app
     * @throws BeanFactoryException
     */
    @Override
    public void init(AppView app) throws BeanFactoryException {

        dbQuery = (DataLogicSystem) app.getBean("uk.kalc.pos.datalogic.DataLogicSystem");

        m_App = app;
        m_TTP = new TicketParser(m_App.getDeviceTicket(), dbQuery);

        m_jTicketTable.setDefaultRenderer(Object.class, new TableRendererBasic(
                new Formats[]{new FormatsPayment(), Formats.CURRENCY}));
        m_jTicketTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        m_jScrollTableTicket.getVerticalScrollBar().setPreferredSize(new Dimension(25, 25));
        m_jTicketTable.getTableHeader().setReorderingAllowed(false);
        m_jTicketTable.setRowHeight(25);
        m_jTicketTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        m_jsalestable.setDefaultRenderer(Object.class, new TableRendererBasic(
                new Formats[]{Formats.STRING, Formats.CURRENCY, Formats.CURRENCY, Formats.CURRENCY}));
        m_jsalestable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        m_jScrollSales.getVerticalScrollBar().setPreferredSize(new Dimension(25, 25));
        m_jsalestable.getTableHeader().setReorderingAllowed(false);
        m_jsalestable.setRowHeight(25);
        m_jsalestable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        m_jCloseCash.setVisible(true);
        jPanelBottom.setVisible(true);
        
        jLabelCards.setVisible(SystemProperty.HANDLINGFEES);
        m_jCards.setVisible(SystemProperty.HANDLINGFEES);

    }

    /**
     *
     * @return
     */
    @Override
    public Object getBean() {
        return this;
    }

    /**
     *
     * @return
     */
    @Override
    public JComponent getComponent() {
        return this;
    }

    /**
     *
     * @return
     */
    @Override
    public String getTitle() {
        return AppLocal.getIntString("menu.closeCash");
    }

    /**
     *
     * @throws BasicException
     */
    @Override
    public void activate() throws BasicException {
        loadData();
    }

    /**
     *
     * @return
     */
    @Override
    public boolean deactivate() {

        return true;
    }

    private void loadData() throws BasicException {

        // Reset
        m_jSequence.setText(null);
        m_jMinDate.setText(null);
        m_jMaxDate.setText(null);
        m_jPrintCash.setEnabled(false);
        m_jCloseCash.setEnabled(false);
        m_jPreviewCash.setEnabled(false);

        m_jCount.setText(null);
        m_jCash.setText(null);

        m_jSales.setText(null);
        m_jSalesSubtotal.setText(null);
        m_jSalesTaxes.setText(null);
        m_jSalesTotal.setText(null);
        m_jGiftCards.setText(null);
        
        m_jCards.setText(null);

        m_jTicketTable.setModel(new DefaultTableModel());
        m_jsalestable.setModel(new DefaultTableModel());

        // LoadData
        m_PaymentsToClose = PaymentsModel.loadInstance(m_App);

        populateData();
        if (m_PaymentsToClose.getPayments() != 0 || m_PaymentsToClose.getSales() != 0) {
            m_jCloseCash.setEnabled(true);
        }

        // read the hourly sales
        hSales = dbQuery.getHourlySales(m_App.getActiveCashIndex());
        // read usersales
        userSales = dbQuery.getUserSales(m_App.getActiveCashIndex());
    }

    private void populateData() {
        // Populate Data
        m_jSequence.setText(m_PaymentsToClose.printSequence());
        m_jMinDate.setText(m_PaymentsToClose.printDateStart());
        m_jMaxDate.setText(m_PaymentsToClose.printDateEnd());

        if (m_PaymentsToClose.getPayments() != 0 || m_PaymentsToClose.getSales() != 0) {

            m_jPrintCash.setEnabled(true);
            m_jPreviewCash.setEnabled(true);

            m_jCount.setText(m_PaymentsToClose.printPayments());
            m_jCash.setText(m_PaymentsToClose.printPaymentsTotal());

            m_jSales.setText(m_PaymentsToClose.printSales());
            m_jSalesSubtotal.setText(m_PaymentsToClose.printSalesBase());
            m_jSalesTaxes.setText(m_PaymentsToClose.printSalesTaxes());
            m_jSalesTotal.setText(m_PaymentsToClose.printSalesTotal());

            m_jGiftCards.setText(m_PaymentsToClose.printGiftCardSalesTotal());
            
            m_jCards.setText(m_PaymentsToClose.printCardFees());
            
            
        }

        m_jTicketTable.setModel(m_PaymentsToClose.getPaymentsModel());

        TableColumnModel jColumns = m_jTicketTable.getColumnModel();
        jColumns.getColumn(0).setPreferredWidth(178);
        jColumns.getColumn(0).setResizable(false);
        jColumns.getColumn(1).setPreferredWidth(80);
        jColumns.getColumn(1).setResizable(false);

        m_jsalestable.setModel(m_PaymentsToClose.getSalesModel());

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        jColumns = m_jsalestable.getColumnModel();
        jColumns.getColumn(0).setPreferredWidth(108);
        jColumns.getColumn(0).setResizable(false);
        jColumns.getColumn(1).setPreferredWidth(60);
        jColumns.getColumn(1).setResizable(false);
        jColumns.getColumn(1).setCellRenderer(centerRenderer);
        jColumns.getColumn(2).setPreferredWidth(75);
        jColumns.getColumn(2).setResizable(false);
        jColumns.getColumn(3).setPreferredWidth(75);
        jColumns.getColumn(3).setResizable(false);

// read number of no sale activations 
        result = dbQuery.getNoSales(m_PaymentsToClose.getStartDate());
        m_jNoCashSales.setText(result.toString());

    }

    public String printUser() {
        return m_App.getAppUserView().getUser().getName();
    }

    private void printPayments(String report) {
        String source = dbQuery.getResourceAsXML(report);
        String sresource;
        IncludeFile incFile = new IncludeFile(source, dbQuery);
        List<String> categories = new ArrayList<>();
        try {
            categories = dbQuery.getParentCategories(m_App.getActiveCashIndex());
        } catch (BasicException ex) {

        }

        if (source == null) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"));
            msg.show(this);
        } else {
            sresource = incFile.processInclude();
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                script.put("payments", m_PaymentsToClose);
                script.put("nosales", result);
                script.put("hourlysales", hSales);
                script.put("user", printUser());
                script.put("sales", dbQuery.getReceiptCount(m_App.getActiveCashIndex()));
                script.put("usersales", userSales);
                script.put("categories", categories);
                script.put("company", new CompanyInfo());
                m_TTP.printTicket(script.eval(sresource).toString());
            } catch (ScriptException | TicketPrinterException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
                msg.show(this);
            }
        }
    }

    private class FormatsPayment extends Formats {

        @Override
        protected String formatValueInt(Object value) {
            return AppLocal.getIntString("paymentdescription." + (String) value);
        }

        @Override
        protected Object parseValueInt(String value) throws ParseException {
            return value;
        }

        @Override
        public int getAlignment() {
            return javax.swing.SwingConstants.LEFT;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        m_jSequence = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        m_jMinDate = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        m_jMaxDate = new javax.swing.JTextField();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jPanel5 = new javax.swing.JPanel();
        m_jScrollTableTicket = new javax.swing.JScrollPane();
        m_jTicketTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        m_jCount = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        m_jCash = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        m_jSales = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        m_jSalesSubtotal = new javax.swing.JTextField();
        m_jScrollSales = new javax.swing.JScrollPane();
        m_jsalestable = new javax.swing.JTable();
        m_jSalesTaxes = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        m_jSalesTotal = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        m_jNoCashSales = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        m_jGiftCards = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabelCards = new javax.swing.JLabel();
        m_jCards = new javax.swing.JTextField();
        jPanelBottom = new javax.swing.JPanel();
        m_jPrintCash = new javax.swing.JButton();
        m_jPreviewCash = new javax.swing.JButton();
        m_jCloseCash = new javax.swing.JButton();
        btnReloadDay = new javax.swing.JButton();
        btnResetDay = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setFont(KALCFonts.DEFAULTFONT.deriveFont(12f));
        jPanel1.setPreferredSize(new java.awt.Dimension(931, 650));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, AppLocal.getIntString("label.datestitle"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, KALCFonts.DEFAULTFONTBOLD)); // NOI18N

        jLabel11.setFont(KALCFonts.DEFAULTFONT);
        jLabel11.setText(AppLocal.getIntString("label.sequence")); // NOI18N

        m_jSequence.setEditable(false);
        m_jSequence.setFont(KALCFonts.DEFAULTFONT);
        m_jSequence.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel2.setFont(KALCFonts.DEFAULTFONT);
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText(AppLocal.getIntString("label.startDate")); // NOI18N

        m_jMinDate.setEditable(false);
        m_jMinDate.setFont(KALCFonts.DEFAULTFONT);
        m_jMinDate.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel3.setFont(KALCFonts.DEFAULTFONT);
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText(AppLocal.getIntString("label.endDate")); // NOI18N

        m_jMaxDate.setEditable(false);
        m_jMaxDate.setFont(KALCFonts.DEFAULTFONT);
        m_jMaxDate.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(48, Short.MAX_VALUE)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jSequence, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jMinDate, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jMaxDate, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jSequence, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jMinDate, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jMaxDate, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 5, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(filler1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, AppLocal.getIntString("label.paymentstitle"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, KALCFonts.DEFAULTFONTBOLD)); // NOI18N
        jPanel5.setFont(KALCFonts.DEFAULTFONT.deriveFont(12f)
        );
        jPanel5.setPreferredSize(new java.awt.Dimension(879, 480));

        m_jScrollTableTicket.setBorder(null);
        m_jScrollTableTicket.setFont(KALCFonts.DEFAULTFONTBOLD);
        m_jScrollTableTicket.setMinimumSize(new java.awt.Dimension(350, 140));
        m_jScrollTableTicket.setPreferredSize(new java.awt.Dimension(350, 140));

        m_jTicketTable.setFont(KALCFonts.DEFAULTFONT.deriveFont(15f));
        m_jTicketTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        m_jTicketTable.setFocusable(false);
        m_jTicketTable.setIntercellSpacing(new java.awt.Dimension(0, 1));
        m_jTicketTable.setRequestFocusEnabled(false);
        m_jScrollTableTicket.setViewportView(m_jTicketTable);

        jLabel1.setFont(KALCFonts.DEFAULTFONT);
        jLabel1.setText(AppLocal.getIntString("label.transactions")); // NOI18N

        m_jCount.setEditable(false);
        m_jCount.setFont(KALCFonts.DEFAULTFONT);
        m_jCount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel4.setFont(KALCFonts.DEFAULTFONTBOLD);
        jLabel4.setText(AppLocal.getIntString("label.Money")); // NOI18N

        m_jCash.setEditable(false);
        m_jCash.setFont(KALCFonts.DEFAULTFONTBOLD);
        m_jCash.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel5.setFont(KALCFonts.DEFAULTFONT);
        jLabel5.setText(AppLocal.getIntString("label.sales")); // NOI18N

        m_jSales.setEditable(false);
        m_jSales.setFont(KALCFonts.DEFAULTFONT);
        m_jSales.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel6.setFont(KALCFonts.DEFAULTFONT);
        jLabel6.setText(AppLocal.getIntString("label.totalnet")); // NOI18N

        m_jSalesSubtotal.setEditable(false);
        m_jSalesSubtotal.setFont(KALCFonts.DEFAULTFONT);
        m_jSalesSubtotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        m_jScrollSales.setBorder(null);
        m_jScrollSales.setFont(KALCFonts.DEFAULTFONT.deriveFont(16f));

        m_jsalestable.setFont(KALCFonts.DEFAULTFONT.deriveFont(15f));
        m_jsalestable.setFocusable(false);
        m_jsalestable.setIntercellSpacing(new java.awt.Dimension(0, 1));
        m_jsalestable.setRequestFocusEnabled(false);
        m_jScrollSales.setViewportView(m_jsalestable);

        m_jSalesTaxes.setEditable(false);
        m_jSalesTaxes.setFont(KALCFonts.DEFAULTFONT);
        m_jSalesTaxes.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel12.setFont(KALCFonts.DEFAULTFONT);
        jLabel12.setText(AppLocal.getIntString("label.taxes")); // NOI18N

        jLabel7.setFont(KALCFonts.DEFAULTFONTBOLD);
        jLabel7.setText(AppLocal.getIntString("label.total")); // NOI18N

        m_jSalesTotal.setEditable(false);
        m_jSalesTotal.setFont(KALCFonts.DEFAULTFONTBOLD);
        m_jSalesTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jSalesTotal.setPreferredSize(new java.awt.Dimension(6, 21));

        jLabel8.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(14f));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText(AppLocal.getIntString("label.nocashsales"));

        m_jNoCashSales.setEditable(false);
        m_jNoCashSales.setFont(KALCFonts.DEFAULTFONT);
        m_jNoCashSales.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        m_jGiftCards.setEditable(false);
        m_jGiftCards.setFont(KALCFonts.DEFAULTFONTBOLD);
        m_jGiftCards.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jGiftCards.setPreferredSize(new java.awt.Dimension(6, 21));

        jLabel9.setFont(KALCFonts.DEFAULTFONTBOLD);
        jLabel9.setText(AppLocal.getIntString("label.giftcards")); // NOI18N

        jLabelCards.setFont(KALCFonts.DEFAULTFONTBOLD);
        jLabelCards.setText(AppLocal.getIntString("label.CardsFees")); // NOI18N

        m_jCards.setEditable(false);
        m_jCards.setFont(KALCFonts.DEFAULTFONTBOLD);
        m_jCards.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(m_jScrollTableTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jScrollSales, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(m_jNoCashSales, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                        .addGap(29, 29, 29))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabelCards, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addGap(2, 2, 2)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(m_jSalesTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jSalesSubtotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jCards, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jCash, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jGiftCards, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(m_jSalesTaxes, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                        .addComponent(m_jSales)
                                        .addComponent(m_jCount, javax.swing.GroupLayout.Alignment.TRAILING)))))))
                .addGap(49, 49, 49))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jCount, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jSales, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jSalesSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jSalesTaxes, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jSalesTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jGiftCards, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelCards, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jCards, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jCash, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jNoCashSales, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(m_jScrollSales, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                    .addComponent(m_jScrollTableTicket, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        m_jPrintCash.setFont(KALCFonts.DEFAULTBUTTONFONT.deriveFont(16f));
        m_jPrintCash.setIcon(IconFactory.getIcon("printer.png"));
        m_jPrintCash.setText(AppLocal.getIntString("button.printCash")); // NOI18N
        m_jPrintCash.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_jPrintCash.setIconTextGap(2);
        m_jPrintCash.setMaximumSize(new java.awt.Dimension(85, 33));
        m_jPrintCash.setMinimumSize(new java.awt.Dimension(85, 33));
        m_jPrintCash.setPreferredSize(new java.awt.Dimension(85, 33));
        m_jPrintCash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jPrintCashActionPerformed(evt);
            }
        });

        m_jPreviewCash.setFont(KALCFonts.DEFAULTBUTTONFONT.deriveFont(16f));
        m_jPreviewCash.setIcon(IconFactory.getIcon("printer.png"));
        m_jPreviewCash.setText(AppLocal.getIntString("button.printCashPreview")); // NOI18N
        m_jPreviewCash.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_jPreviewCash.setIconTextGap(2);
        m_jPreviewCash.setMaximumSize(new java.awt.Dimension(85, 33));
        m_jPreviewCash.setMinimumSize(new java.awt.Dimension(85, 33));
        m_jPreviewCash.setPreferredSize(new java.awt.Dimension(85, 33));
        m_jPreviewCash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jPreviewCashActionPerformed(evt);
            }
        });

        m_jCloseCash.setFont(KALCFonts.DEFAULTBUTTONFONT.deriveFont(16f));
        m_jCloseCash.setIcon(IconFactory.getIcon("calculator.png"));
        m_jCloseCash.setText(AppLocal.getIntString("button.closeCash")); // NOI18N
        m_jCloseCash.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_jCloseCash.setIconTextGap(2);
        m_jCloseCash.setInheritsPopupMenu(true);
        m_jCloseCash.setMaximumSize(new java.awt.Dimension(85, 33));
        m_jCloseCash.setMinimumSize(new java.awt.Dimension(85, 33));
        m_jCloseCash.setPreferredSize(new java.awt.Dimension(85, 33));
        m_jCloseCash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCloseCashActionPerformed(evt);
            }
        });

        btnReloadDay.setFont(KALCFonts.DEFAULTBUTTONFONT.deriveFont(16f)
        );
        btnReloadDay.setIcon(IconFactory.getIcon("reload.png"));
        btnReloadDay.setText(AppLocal.getIntString("button.reloadreport")); // NOI18N
        btnReloadDay.setMaximumSize(new java.awt.Dimension(85, 33));
        btnReloadDay.setMinimumSize(new java.awt.Dimension(85, 33));
        btnReloadDay.setPreferredSize(new java.awt.Dimension(85, 33));
        btnReloadDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReloadDayActionPerformed(evt);
            }
        });

        btnResetDay.setFont(KALCFonts.DEFAULTBUTTONFONT.deriveFont(16f));
        btnResetDay.setIcon(IconFactory.getIcon("reload.png"));
        btnResetDay.setText(AppLocal.getIntString("button.reset")); // NOI18N
        btnResetDay.setMaximumSize(new java.awt.Dimension(85, 33));
        btnResetDay.setMinimumSize(new java.awt.Dimension(85, 33));
        btnResetDay.setPreferredSize(new java.awt.Dimension(85, 33));
        btnResetDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetDayActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelBottomLayout = new javax.swing.GroupLayout(jPanelBottom);
        jPanelBottom.setLayout(jPanelBottomLayout);
        jPanelBottomLayout.setHorizontalGroup(
            jPanelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelBottomLayout.createSequentialGroup()
                .addComponent(btnReloadDay, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnResetDay, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jPreviewCash, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jPrintCash, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jCloseCash, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelBottomLayout.setVerticalGroup(
            jPanelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBottomLayout.createSequentialGroup()
                .addGroup(jPanelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_jPrintCash, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jPreviewCash, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jCloseCash, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelBottomLayout.createSequentialGroup()
                .addGroup(jPanelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnReloadDay, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                    .addComponent(btnResetDay, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanelBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        add(jPanel1, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jCloseCashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jCloseCashActionPerformed

        if (JAlertPane.messageBox(JAlertPane.CONFIRMATION, AppLocal.getIntString("message.wannaclosecash"), 16,
                new Dimension(125, 50), JAlertPane.YES_NO_OPTION) == 5) {

            Date dNow = new Date();

            try {
                if (m_App.getActiveCashDateEnd() == null) {
                    new StaticSentence(m_App.getSession(), "update closedcash set dateend = ?, nosales = ? where host = ? and money = ? ",
                            new SerializerWriteBasic(new Datas[]{Datas.TIMESTAMP, Datas.INT, Datas.STRING, Datas.STRING}))
                            .exec(new Object[]{dNow, result, m_App.getProperties().getHost(), m_App.getActiveCashIndex()});
                }
            } catch (BasicException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotclosecash"), e);
                msg.show(this);
            }

            try {
                m_App.setActiveCash(UUID.randomUUID().toString(), m_App.getActiveCashSequence() + 1, dNow, null);

                dbQuery.execInsertCash(new Object[]{m_App.getActiveCashIndex(), m_App.getProperties().getHost(),
                    m_App.getActiveCashSequence(), m_App.getActiveCashDateStart(), m_App.getActiveCashDateEnd()});

                dbQuery.execDrawerOpened(new Object[]{UUID.randomUUID().toString(),
                    m_App.getAppUserView().getUser().getId(), "Close Cash"});

                m_PaymentsToClose.setDateEnd(dNow);

                printPayments("Printer.CloseCash");

                JAlertPane.messageBox(new Dimension(300, 100), JAlertPane.INFORMATION, AppLocal.getIntString("message.closecashok"), 16,
                        new Dimension(125, 50), JAlertPane.OK_OPTION);
            } catch (BasicException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotclosecash"), e);
                msg.show(this);
            }

            try {
                loadData();
            } catch (BasicException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("label.noticketstoclose"), e);
                msg.show(this);
            }
        }
    }//GEN-LAST:event_m_jCloseCashActionPerformed

    private void m_jPrintCashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jPrintCashActionPerformed

        if (reload) {
            printPayments("Printer.CloseCash");
        } else {
            printPayments("Printer.PartialCash");
        }


    }//GEN-LAST:event_m_jPrintCashActionPerformed

    private void m_jPreviewCashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jPreviewCashActionPerformed
        printPayments("Printer.CloseCash.Preview");
    }//GEN-LAST:event_m_jPreviewCashActionPerformed

    private void btnReloadDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReloadDayActionPerformed
        Object[] result = JAlertPane.closedCashBox();

        if ((int) result[0] != 0) {
            m_jCloseCash.setEnabled(true);
            return;
        }

        m_jCloseCash.setEnabled(false);
        try {
            reload = true;
            m_PaymentsToClose = PaymentsModel.loadInstance((ClosedCashInfo) result[1]);
            populateData();
            hSales = dbQuery.getHourlySales(((ClosedCashInfo) result[1]).getMoneyGuid());
            userSales = dbQuery.getUserSales(((ClosedCashInfo) result[1]).getMoneyGuid());

        } catch (BasicException ex) {
            reload = false;
            System.out.println("error thrown");
            m_jCloseCash.setEnabled(true);
        }
    }//GEN-LAST:event_btnReloadDayActionPerformed

    private void btnResetDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetDayActionPerformed
        try {
            reload = false;
            m_PaymentsToClose = PaymentsModel.loadInstance(m_App);
            populateData();
            if (m_PaymentsToClose.getPayments() != 0 || m_PaymentsToClose.getSales() != 0) {
                m_jCloseCash.setEnabled(true);
            }
            // read the hourly sales
            hSales = dbQuery.getHourlySales(m_App.getActiveCashIndex());
            // read usersales
            userSales = dbQuery.getUserSales(m_App.getActiveCashIndex());

        } catch (BasicException ex) {
            Logger.getLogger(JPanelCloseMoney.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnResetDayActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnReloadDay;
    private javax.swing.JButton btnResetDay;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelCards;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelBottom;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField m_jCards;
    private javax.swing.JTextField m_jCash;
    private javax.swing.JButton m_jCloseCash;
    private javax.swing.JTextField m_jCount;
    private javax.swing.JTextField m_jGiftCards;
    private javax.swing.JTextField m_jMaxDate;
    private javax.swing.JTextField m_jMinDate;
    private javax.swing.JTextField m_jNoCashSales;
    private javax.swing.JButton m_jPreviewCash;
    private javax.swing.JButton m_jPrintCash;
    private javax.swing.JTextField m_jSales;
    private javax.swing.JTextField m_jSalesSubtotal;
    private javax.swing.JTextField m_jSalesTaxes;
    private javax.swing.JTextField m_jSalesTotal;
    private javax.swing.JScrollPane m_jScrollSales;
    private javax.swing.JScrollPane m_jScrollTableTicket;
    private javax.swing.JTextField m_jSequence;
    private javax.swing.JTable m_jTicketTable;
    private javax.swing.JTable m_jsalestable;
    // End of variables declaration//GEN-END:variables

}
