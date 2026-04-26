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
package uk.kalc.pos.customers;

import uk.kalc.pos.datalogic.DataLogicCustomers;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import uk.kalc.basic.BasicException;
import uk.kalc.commons.dialogs.JAlertPane;
import uk.kalc.custom.CustomColour;
import uk.kalc.custom.CustomJLabel;
import uk.kalc.custom.CustomJTextField;
import uk.kalc.custom.ExtendedJButton;
import uk.kalc.data.loader.QBFCompareEnum;
import uk.kalc.data.user.ListProvider;
import uk.kalc.data.user.ListProviderCreator;
import uk.kalc.globals.IconFactory;
import uk.kalc.osk.KeyBoard;
import uk.kalc.pos.forms.AppLocal;
import uk.kalc.pos.forms.KALCFonts;
import uk.kalc.pos.forms.JRootFrame;

/**
 * @author John Lewis
 */
public class JCustomerFinder extends JDialog {

//        private final Font txtFont = StartPOS.DEFAULTFONT.deriveFont(18f);
//
//    private final Font btnFont = StartPOS.DEFAULTFONT.deriveFont(
//            new HashMap<TextAttribute, Object>() {
//        {
//            put(TextAttribute.SIZE, 18);
//            put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
//        }
//    }
//    );
//
//    private final Font lblFont = btnFont.deriveFont(16f);
    //Set the fonts to be used
    private final Font lblFont = KALCFonts.DEFAULTFONTBOLD;
    private final Font txtFont = KALCFonts.DEFAULTFONT.deriveFont(18f);
    private final Font btnFont = KALCFonts.DEFAULTFONTBOLD;
    private final Font listFont = KALCFonts.DEFAULTFONTBOLD;

    //Main panels to be used by miglayout
    private final JPanel mainPanel = new JPanel(new MigLayout("insets 10 10 10 10 ", "", ""));
    private final JPanel customerFinder = new JPanel(new MigLayout("insets 0 0 0 0 ", "[120][220]15[420]", "[][][]"));

    private final JPanel listPane = new JPanel();
    private final JScrollPane jScrollPane1 = new JScrollPane();
    private final JList jListItems = new JList();

    private JPanel btnMainPanel;
    private JPanel btnPanel;

    private ExtendedJButton btn;
    private JButton btnOK;
    private final JPanel keyBoard;

    private final CustomJTextField m_jtxtTaxID = new CustomJTextField(new Dimension(300, 25), txtFont);
    private final CustomJTextField m_jtxtPostal = new CustomJTextField(new Dimension(300, 25), txtFont);
    private final CustomJTextField m_jtxtName = new CustomJTextField(new Dimension(300, 25), txtFont);
    private final CustomJTextField m_jtxtPhone = new CustomJTextField(new Dimension(300, 25), txtFont);
    private final CustomJTextField m_jtxtEmail = new CustomJTextField(new Dimension(300, 25), txtFont);

    private CustomerInfo selectedCustomer = null;
    private ListProvider lpr;

    public JCustomerFinder(DataLogicCustomers dlCustomers, String siteguid) {
        super(new JFrame());
        keyBoard = KeyBoard.getKeyboard(KeyBoard.Layout.QWERTY);
        customerFinderPane(dlCustomers, siteguid);
        pack();
        int x = JRootFrame.PARENTFRAME.getX() + ((JRootFrame.PARENTFRAME.getWidth() - this.getWidth()) / 2);
        int y = JRootFrame.PARENTFRAME.getY() + 50 + 25;
        setLocation(x, y);
    }

    public JCustomerFinder(DataLogicCustomers dlCustomers) {
        super(new JFrame());
        keyBoard = KeyBoard.getKeyboard(KeyBoard.Layout.QWERTY);
        customerFinderPane(dlCustomers, null);
        pack();
        int x = JRootFrame.PARENTFRAME.getX() + ((JRootFrame.PARENTFRAME.getWidth() - this.getWidth()) / 2);
        int y = JRootFrame.PARENTFRAME.getY() + 75;
        setLocation(x, y);
    }

    protected void customerFinderPane(DataLogicCustomers dlCustomers, String siteGuid) {
        lpr = new ListProviderCreator(dlCustomers.getCustomerList(siteGuid));
        try {
            jListItems.setModel(new MyListData(lpr.loadData()));
        } catch (BasicException ex) {
            Logger.getLogger(JCustomerFinder.class.getName()).log(Level.SEVERE, null, ex);
        }

        createListPanel(400, 100);
        setButtonPanel(new Dimension(105, 35));
        btnOK.setEnabled(false);

        customerFinder.add(new CustomJLabel(AppLocal.getIntString("label.taxid"), lblFont));
        customerFinder.add(m_jtxtTaxID);
        customerFinder.add(listPane, "span 1 6, wrap, growy");
        customerFinder.add(new CustomJLabel(AppLocal.getIntString("label.postal"), lblFont));
        customerFinder.add(m_jtxtPostal, "wrap");
        customerFinder.add(new CustomJLabel(AppLocal.getIntString("label.name"), lblFont));
        customerFinder.add(m_jtxtName, "wrap");
        customerFinder.add(new CustomJLabel(AppLocal.getIntString("label.telephone"), lblFont));
        customerFinder.add(m_jtxtPhone, "wrap");
        customerFinder.add(new CustomJLabel(AppLocal.getIntString("label.companyemail"), lblFont));
        customerFinder.add(m_jtxtEmail, "wrap");
        customerFinder.add(btnPanel, "span 2,  align right, wrap");
        customerFinder.add(btnMainPanel, "span ,  align right");
        mainPanel.add(customerFinder, "align center, wrap");

        setResizable(false);
        setModal(true);

        mainPanel.setBorder(BorderFactory.createLineBorder(CustomColour.getBorderColour(), 2));
        setTitle("Customer Finder");
        getContentPane().add(mainPanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                selectedCustomer = null;
            }
        });
        setAlwaysOnTop(true);
    }

    private void createListPanel(Integer width, Integer height) {

        listPane.setPreferredSize(new Dimension(width, height));
        listPane.setLayout(new java.awt.BorderLayout());
        listPane.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jListItems.setCellRenderer(new CustomerRenderer());
        jListItems.setFont(listFont);
        jListItems.setFixedCellHeight(40);
        jListItems.setFocusable(false);
        jListItems.setRequestFocusEnabled(false);
        jListItems.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    selectedCustomer = (CustomerInfo) jListItems.getSelectedValue();
                    dispose();
                }
            }
        });

        jListItems.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                btnOK.setEnabled(jListItems.getSelectedValue() != null);
            }
        });

        jScrollPane1.getVerticalScrollBar().setPreferredSize(new Dimension(35, 35));
        jScrollPane1.setViewportView(jListItems);

    }

    public CustomerInfo getSelectedCustomer() {
        return selectedCustomer;
    }

    public void search(CustomerInfo customer) {
        if (customer == null || customer.getName() == null || customer.getName().equals("")) {
            resetFields();
            cleanSearch();
        } else {
            m_jtxtTaxID.setText(customer.getTaxid());
            m_jtxtName.setText(customer.getName());
            m_jtxtPostal.setText(customer.getPostal());
            m_jtxtPhone.setText(customer.getPhone());
            m_jtxtEmail.setText(customer.getEmail());

            executeSearch();
        }
    }

    public void executeSearch() {
        try {
            jListItems.setModel(new MyListData(lpr.setData(createValue())));

            if (jListItems.getModel().getSize() > 0) {
                jListItems.setSelectedIndex(0);
            }
        } catch (BasicException e) {
        }
    }

    private void cleanSearch() {
        jListItems.setModel(new MyListData(new ArrayList()));
    }

    private void resetFields() {
        m_jtxtTaxID.setText("");
        m_jtxtPostal.setText("");
        m_jtxtName.setText("");
        m_jtxtPhone.setText("");
        m_jtxtEmail.setText("");
        m_jtxtTaxID.requestFocus();
        cleanSearch();
    }

    private Object createValue() throws BasicException {

        Object[] afilter = new Object[10];

        // TaxID
        if (m_jtxtTaxID.getText() == null || m_jtxtTaxID.getText().equals("")) {
            afilter[0] = QBFCompareEnum.COMP_NONE;
            afilter[1] = null;
        } else {
            afilter[0] = QBFCompareEnum.COMP_RE;
            afilter[1] = "%" + m_jtxtTaxID.getText() + "%";
        }

        // Name
        if (m_jtxtName.getText() == null || m_jtxtName.getText().equals("")) {
            afilter[2] = QBFCompareEnum.COMP_NONE;
            afilter[3] = null;
        } else {
            afilter[2] = QBFCompareEnum.COMP_RE;
            afilter[3] = "%" + m_jtxtName.getText() + "%";
        }

        // Postal
        if (m_jtxtPostal.getText() == null || m_jtxtPostal.getText().equals("")) {
            afilter[4] = QBFCompareEnum.COMP_NONE;
            afilter[5] = null;
        } else {
            afilter[4] = QBFCompareEnum.COMP_RE;
            afilter[5] = "%" + m_jtxtPostal.getText() + "%";
        }

        // Phone
        if (m_jtxtPhone.getText() == null || m_jtxtPhone.getText().equals("")) {
            afilter[6] = QBFCompareEnum.COMP_NONE;
            afilter[7] = null;
        } else {
            afilter[6] = QBFCompareEnum.COMP_RE;
            afilter[7] = "%" + m_jtxtPhone.getText() + "%";
        }

        // Email
        if (m_jtxtEmail.getText() == null || m_jtxtEmail.getText().equals("")) {
            afilter[8] = QBFCompareEnum.COMP_NONE;
            afilter[9] = null;
        } else {
            afilter[8] = QBFCompareEnum.COMP_RE;
            afilter[9] = "%" + m_jtxtEmail.getText() + "%";
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

    private void setButtonPanel(Dimension dimension) {
        btnPanel = new JPanel();

        btn = new ExtendedJButton("Reset", "reload.png", 6);
        btn.setPreferredSize(dimension);
        btn.setFont(btnFont);
        btn.setFocusable(false);
        btn.addActionListener((ActionEvent e) -> {
            resetFields();
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
            selectedCustomer = (CustomerInfo) jListItems.getSelectedValue();
            dispose();
        });
        btnMainPanel.add(btnOK);

        btn = new ExtendedJButton(AppLocal.getIntString("button.cancel"), JAlertPane.CANCEL);
        btn.setPreferredSize(dimension);
        btn.setFont(btnFont);
        btn.setFocusable(false);
        btn.addActionListener((ActionEvent e) -> {
            selectedCustomer = null;
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
            mainPanel.add(keyBoard);
            int x = (this.getX() + (this.getWidth() / 2)) - 411;
            this.setLocation(x, this.getY());
            this.pack();
        });
        btnMainPanel.add(kbButton);
    }

}
