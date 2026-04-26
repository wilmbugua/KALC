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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.NumberFormatter;
import net.miginfocom.swing.MigLayout;
import org.jdatepicker.JDatePicker;
import uk.kalc.basic.BasicException;
import uk.kalc.commons.dialogs.JAlertPane;
import uk.kalc.custom.CustomJLabel;
import uk.kalc.custom.CustomJTextField;
import uk.kalc.custom.ExtendedJButton;
import uk.kalc.data.gui.ComboBoxValModel;
import uk.kalc.data.gui.ListQBFModelNumber;
import uk.kalc.data.gui.MessageInf;
import uk.kalc.data.loader.QBFCompareEnum;
import uk.kalc.data.loader.SentenceList;
import uk.kalc.data.user.ListProvider;
import uk.kalc.data.user.ListProviderCreator;
import uk.kalc.globals.IconFactory;
import uk.kalc.globals.SystemProperty;
import uk.kalc.osk.KeyBoard;
import uk.kalc.pos.datalogic.DataLogicCustomers;
import uk.kalc.pos.customers.JCustomerFinder;
import uk.kalc.pos.forms.AppLocal;
import uk.kalc.pos.datalogic.DataLogicSales;
import uk.kalc.pos.forms.KALCFonts;
import uk.kalc.pos.forms.JRootFrame;
import uk.kalc.pos.inventory.TaxCategoryInfo;
import uk.kalc.pos.ticket.FindTicketsInfo;
import uk.kalc.pos.ticket.FindTicketsRenderer;
import uk.kalc.pos.ticket.TicketType;

public class JTicketsFinder extends JDialog {

    //Set the fonts to be used
    private final Font lblFont = KALCFonts.DEFAULTFONTBOLD;
    private final Font txtFont = KALCFonts.DEFAULTFONTBOLD;
    private final Font btnFont = KALCFonts.DEFAULTFONTBOLD;
    private final Font listFont = KALCFonts.DEFAULTFONTBOLD;

    //Main panels to be used by miglayout
    private final JPanel mainPanel = new JPanel(new MigLayout("insets 10 10 10 10 ", "", ""));
    private final JPanel ticketFinder = new JPanel(new MigLayout("insets 0 0 0 0 ", "[100][330]", "[][][]"));
    private final JPanel keyboardPanel = new JPanel();

    private final JPanel listPane = new JPanel();
    private final JScrollPane jScrollPane1 = new JScrollPane();
    private final JList jListItems = new javax.swing.JList();

    private JPanel btnMainPanel;
    private JPanel btnPanel;

    private ExtendedJButton btn;
    private JButton btnOK;
    private final JButton customerBtn = new JButton();

    private JFormattedTextField jtxtTicketID;
    private JFormattedTextField jtxtMoney;

    private final CustomJTextField jtxtCustomer = new CustomJTextField(new Dimension(300, 25), txtFont);

    private final JDatePicker startDate = new JDatePicker();
    private final JDatePicker endDate = new JDatePicker();

    private ListProvider lpr;

    private final JPanel keyBoard;

    private SentenceList m_sentcat;
    private ComboBoxValModel m_CategoryModel;
    private DataLogicSales dlSales;
    private DataLogicCustomers dlCustomers;
    private FindTicketsInfo selectedTicket;
    private final JComboBox jComboBoxTicket = new JComboBox();
    private final JComboBox jcboUser = new JComboBox();
    private final JComboBox jcboMoney = new JComboBox();

    public JTicketsFinder(DataLogicSales dlSales, DataLogicCustomers dlCustomers) {
        super(new JFrame());
        keyBoard = KeyBoard.getKeyboard2(KeyBoard.Layout.QWERTY);
        ticketFinderPane(dlSales, dlCustomers);
        pack();
        int x = JRootFrame.PARENTFRAME.getX() + ((JRootFrame.PARENTFRAME.getWidth() - this.getWidth()) / 2);
        int y = JRootFrame.PARENTFRAME.getY() + 50;
        setLocation(x, y);
    }

    private void ticketFinderPane(DataLogicSales dlSales, DataLogicCustomers dlCustomers) {
        this.dlSales = dlSales;
        this.dlCustomers = dlCustomers;

        setButtonPanel(new Dimension(100, 35));

        lpr = new ListProviderCreator(dlSales.getTicketsList());

        NumberFormat integerFieldFormatter = NumberFormat.getIntegerInstance();
        integerFieldFormatter.setGroupingUsed(false);
        NumberFormatter formatter = new NumberFormatter(integerFieldFormatter) {
            @Override
            public Object stringToValue(String text) throws ParseException {
                if (text.length() == 0) {
                    return null;
                }
                return super.stringToValue(text);
            }
        };
        formatter.setAllowsInvalid(false);
        formatter.setMinimum(0);
        formatter.setCommitsOnValidEdit(true);
        jtxtTicketID = new JFormattedTextField(formatter);
        jtxtTicketID.setPreferredSize(new Dimension(300, 25));

        jComboBoxTicket.setFont(lblFont);
        jcboUser.setFont(lblFont);
        jcboMoney.setFont(lblFont);
        initCombos();

        NumberFormat doubleFieldFormatter = NumberFormat.getNumberInstance();
        doubleFieldFormatter.setGroupingUsed(false);
        NumberFormatter format = new NumberFormatter(doubleFieldFormatter) {
            @Override
            public Object stringToValue(String text) throws ParseException {
                if (text.length() == 0) {
                    return null;
                }
                return super.stringToValue(text);
            }
        };
        format.setAllowsInvalid(false);
        format.setMinimum(0);
        format.setCommitsOnValidEdit(true);
        jtxtMoney = new JFormattedTextField(format);
        jtxtMoney.setPreferredSize(new Dimension(110, 25));

        createListPanel(440, 130);
        customerBtn.setPreferredSize(new Dimension(27, 27));
        customerBtn.setMaximumSize(new Dimension(27, 27));
        customerBtn.setIcon(IconFactory.getResizedIcon("customer_sml.png", new Dimension(25, 25)));

        customerBtn.addActionListener((ActionEvent e) -> {
            this.setVisible(false);
            JCustomerFinder finder = new JCustomerFinder(dlCustomers);
            finder.search(null);
            finder.setVisible(true);
            try {
                jtxtCustomer.setText(finder.getSelectedCustomer() == null
                        ? null
                        : dlSales.loadCustomerExt(finder.getSelectedCustomer().getId()).toString());
            } catch (BasicException ex) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfindcustomer"), e);
                msg.show(this);
            }
            this.setVisible(true);
        });

        btnOK.setEnabled(false);

        startDate.setFont(KALCFonts.DEFAULTFONT);
        startDate.setPreferredSize(new Dimension(300, 25));
        endDate.setFont(KALCFonts.DEFAULTFONT);
        endDate.setPreferredSize(new Dimension(300, 25));
        jcboUser.setPreferredSize(new Dimension(290, 25));

        ticketFinder.add(new CustomJLabel(AppLocal.getIntString("label.ticketid"), lblFont));
        ticketFinder.add(jtxtTicketID, "split 2, width 140:140:140");
        ticketFinder.add(jComboBoxTicket, "align right, gapleft 10, width 150:150:150, gaptop 2");
        ticketFinder.add(listPane, "span 1 7, wrap, growy");
        ticketFinder.add(new CustomJLabel(AppLocal.getIntString("label.startDate"), lblFont));
        ticketFinder.add(startDate, "wrap");
        ticketFinder.add(new CustomJLabel(AppLocal.getIntString("label.endDate"), lblFont));
        ticketFinder.add(endDate, "wrap");
        ticketFinder.add(new CustomJLabel(AppLocal.getIntString("label.customer"), lblFont));
        ticketFinder.add(jtxtCustomer, "split 2, width 272:272:272");
        ticketFinder.add(customerBtn, "gapleft 1, width 25:25:25, wrap");
        ticketFinder.add(new CustomJLabel(AppLocal.getIntString("label.user"), lblFont));
        ticketFinder.add(jcboUser, "span 2, width 298:298:298, wrap");
        ticketFinder.add(new CustomJLabel(AppLocal.getIntString("label.total"), lblFont));
        ticketFinder.add(jcboMoney, "split 2, height 25:25:25, width 145:145:145");
        ticketFinder.add(jtxtMoney, "align right, gapleft 10, width 145:145:145, height 25:25:25, wrap");

        ticketFinder.add(btnPanel, "span 2,  align right, wrap");
        ticketFinder.add(btnMainPanel, "span ,  align right");
        mainPanel.add(ticketFinder, "align center, wrap");
        mainPanel.add(keyboardPanel);

        setResizable(false);
        setModal(true);

        mainPanel.setBorder(BorderFactory.createLineBorder(getBorderColour(), 2));
        setTitle("Ticket Finder");
        getContentPane().add(mainPanel);

        defaultValues();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                selectedTicket = null;
                dispose();
            }
        });
        setAlwaysOnTop(true);

    }

    private void createListPanel(Integer width, Integer height) {

        listPane.setPreferredSize(new Dimension(width, height));
        listPane.setLayout(new java.awt.BorderLayout());
        listPane.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jListItems.setCellRenderer(new FindTicketsRenderer());
        jListItems.setFont(listFont);
        jListItems.setFixedCellHeight(60);
        jListItems.setFocusable(false);
        jListItems.setRequestFocusEnabled(false);
        jListItems.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    selectedTicket = (FindTicketsInfo) jListItems.getSelectedValue();
                    dispose();
                }
            }
        });

        jListItems.addListSelectionListener((javax.swing.event.ListSelectionEvent evt) -> {
            btnOK.setEnabled(jListItems.getSelectedValue() != null);
        });

        jScrollPane1.getVerticalScrollBar().setPreferredSize(new Dimension(35, 35));
        jScrollPane1.setViewportView(jListItems);

    }

    private void setButtonPanel(Dimension dimension) {
        btnPanel = new JPanel();

        btn = new ExtendedJButton("Reset", "reload.png", 6);
        btn.setPreferredSize(dimension);
        btn.setFont(btnFont);
        btn.setFocusable(false);
        btn.addActionListener((ActionEvent e) -> {;
            defaultValues();
        });
        btnPanel.add(btn);

        btn = new ExtendedJButton("Search", "ok.png", 5);
        btn.setPreferredSize(dimension);
        btn.setFont(btnFont);
        btn.setFocusable(false);
        btn.addActionListener((ActionEvent e) -> {
            executeSearch();
        });
        btnPanel.add(btn);

        btnMainPanel = new JPanel();

        btnOK = new ExtendedJButton(AppLocal.getIntString("button.ok"), JAlertPane.OK);
        btnOK.setPreferredSize(dimension);
        btnOK.setFont(btnFont);
        btnOK.setFocusable(false);
        btnOK.addActionListener((ActionEvent e) -> {
            selectedTicket = (FindTicketsInfo) jListItems.getSelectedValue();
            dispose();
        });
        btnMainPanel.add(btnOK);

        btn = new ExtendedJButton(AppLocal.getIntString("button.cancel"), JAlertPane.CANCEL);
        btn.setPreferredSize(dimension);
        btn.setFont(btnFont);
        btn.setFocusable(false);
        btn.addActionListener((ActionEvent e) -> {
            selectedTicket = null;
            dispose();
        });
        btnMainPanel.add(btn);

        JButton kbButton = new JButton();
        kbButton.setBorderPainted(false);
        kbButton.setOpaque(false);
        kbButton.setPreferredSize(new Dimension(75, 35));
        kbButton.setIcon(IconFactory.getResizedIcon("keyboard.png", new Dimension(75, 35)));
        kbButton.addActionListener((ActionEvent e) -> {
            kbButton.setEnabled(false);
            keyboardPanel.add(keyBoard);
            int x = (this.getX() + (this.getWidth() / 2)) - 400;
            int y = this.getY() + this.getHeight() + 10;
            this.setLocation(x, this.getY());
            this.pack();
        });

        btnMainPanel.add(kbButton);

    }

    public FindTicketsInfo getSelectedTicket() {
        return selectedTicket;
    }

    /**
     *
     */
    public void executeSearch() {
        try {
            jListItems.setModel(new MyListData(lpr.setData(createValue())));
            if (jListItems.getModel().getSize() > 0) {
                jListItems.setSelectedIndex(0);
            }
        } catch (BasicException e) {
        }
    }

    private void initCombos() {
        String[] values = new String[]{AppLocal.getIntString("label.sales"),
            AppLocal.getIntString("label.refunds"), AppLocal.getIntString("label.all")};
        jComboBoxTicket.setModel(new DefaultComboBoxModel(values));

        jcboMoney.setModel(ListQBFModelNumber.getMandatoryNumber());

        m_sentcat = dlSales.getUserList();
        m_CategoryModel = new ComboBoxValModel();

        List catlist = null;
        try {
            catlist = m_sentcat.list();
        } catch (BasicException ex) {
            ex.getMessage();
        }
        catlist.add(0, null);
        m_CategoryModel = new ComboBoxValModel(catlist);
        jcboUser.setModel(m_CategoryModel);
    }

    private void defaultValues() {
        jListItems.setModel(new MyListData(new ArrayList()));
        jcboUser.setSelectedItem(null);
        startDate.getModel().setValue(null);
        endDate.getModel().setValue(null);
        jtxtCustomer.setText(null);
        jComboBoxTicket.setSelectedIndex(0);
        jcboUser.setSelectedItem(null);

        jtxtTicketID.setText(null);
        jcboMoney.setSelectedItem(((ListQBFModelNumber) jcboMoney.getModel()).getElementAt(0));
        jcboMoney.revalidate();
        jcboMoney.repaint();

        jtxtMoney.setValue(null);
        jtxtTicketID.setValue(null);
        startDate.getModel().setValue(null);
        endDate.getModel().setValue(null);
        jtxtCustomer.setText(null);
        jtxtTicketID.requestFocus();
    }

    public Object createValue() throws BasicException {

        Object[] afilter = new Object[14];

        // Ticket ID
        if (jtxtTicketID.getText().isEmpty()) {
            afilter[0] = QBFCompareEnum.COMP_NONE;
            afilter[1] = null;
        } else {
            afilter[0] = QBFCompareEnum.COMP_EQUALS;
            afilter[1] = Integer.valueOf(jtxtTicketID.getText());
        }

        // Sale and refund checkbox        
        if (jComboBoxTicket.getSelectedIndex() == 2) {
            afilter[2] = QBFCompareEnum.COMP_DISTINCT;
            afilter[3] = 2;
        } else if (jComboBoxTicket.getSelectedIndex() == 0) {
            afilter[2] = QBFCompareEnum.COMP_EQUALS;
            afilter[3] = TicketType.NORMAL.getId();
        } else if (jComboBoxTicket.getSelectedIndex() == 1) {
            afilter[2] = QBFCompareEnum.COMP_EQUALS;
            afilter[3] = TicketType.REFUND.getId();
        }

        // Receipt money
        afilter[5] = (jtxtMoney.getText().isEmpty()) ? null : Double.valueOf(jtxtMoney.getText());
        afilter[4] = afilter[5] == null ? QBFCompareEnum.COMP_NONE : jcboMoney.getSelectedItem();

        // Date range
        Calendar cal = Calendar.getInstance();
        afilter[6] = (startDate.getModel().getValue() == null) ? QBFCompareEnum.COMP_NONE : QBFCompareEnum.COMP_GREATEROREQUALS;
        cal.set(startDate.getModel().getYear(), startDate.getModel().getMonth(), startDate.getModel().getDay());
        afilter[7] = cal.getTime();
        afilter[8] = (endDate.getModel().getValue() == null) ? QBFCompareEnum.COMP_NONE : QBFCompareEnum.COMP_LESS;
        cal.set(endDate.getModel().getYear(), endDate.getModel().getMonth(), endDate.getModel().getDay());
        afilter[9] = cal.getTime();

        //User
        if (jcboUser.getSelectedItem() == null) {
            afilter[10] = QBFCompareEnum.COMP_NONE;
            afilter[11] = null;
        } else {
            afilter[10] = QBFCompareEnum.COMP_EQUALS;
            afilter[11] = ((TaxCategoryInfo) jcboUser.getSelectedItem()).getName();
        }

        //Customer
        if (jtxtCustomer.getText() == null || jtxtCustomer.getText().equals("")) {
            afilter[12] = QBFCompareEnum.COMP_NONE;
            afilter[13] = null;
        } else {
            afilter[12] = QBFCompareEnum.COMP_RE;
            afilter[13] = "%" + jtxtCustomer.getText() + "%";
        }

        return afilter;

    }

    private static class MyListData extends javax.swing.AbstractListModel {

        private final java.util.List m_data;

        public MyListData(java.util.List data) {
            m_data = data;
        }

        @Override
        public Object getElementAt(int index) {
            return m_data.get(index);
        }

        @Override
        public int getSize() {
            return m_data.size();
        }
    }

    private Color getBorderColour() {
        if (SystemProperty.LAF.equalsIgnoreCase("com.jtattoo.plaf.hifi.HiFiLookAndFeel")) {
            return Color.WHITE;
        }
        return Color.BLACK;
    }
}
