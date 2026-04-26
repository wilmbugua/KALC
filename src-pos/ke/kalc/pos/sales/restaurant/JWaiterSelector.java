/*
**    KALC POS  - Professional Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous Openbravo POS related works   
**
**    https://www.KALC.co.uk
**   
**
 */
package ke.kalc.pos.sales.restaurant;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.miginfocom.swing.MigLayout;
import ke.kalc.basic.BasicException;
import ke.kalc.commons.dialogs.JAlertPane;
import ke.kalc.custom.CustomColour;
import ke.kalc.custom.ExtendedJButton;
import ke.kalc.data.user.ListProvider;
import ke.kalc.pos.customers.CustomerRenderer;
import ke.kalc.pos.datalogic.DataLogicSystem;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.KALCFonts;
import ke.kalc.pos.forms.JRootFrame;
import java.util.logging.Level;
import java.util.logging.Logger;
import ke.kalc.data.user.ListProviderCreator;
import ke.kalc.pos.ticket.UserInfo;

/**
 *
 * @author John
 */
public final class JWaiterSelector extends JDialog {

    private final Font btnFont = KALCFonts.DEFAULTFONTBOLD;
    private final Font listFont = KALCFonts.DEFAULTFONTBOLD;

    //Main panels to be used by miglayout
    private final JPanel mainPanel = new JPanel(new MigLayout("insets 10 10 10 10 ", "", ""));
    private final JPanel waiterFinder = new JPanel(new MigLayout("insets 0 0 0 0 ", "15[420]", "[][][]"));

    private final JPanel listPane = new JPanel();
    private final JScrollPane jScrollPane1 = new JScrollPane();
    private final JList jListItems = new JList();

    private JPanel btnMainPanel;
    private JPanel btnPanel;

    private ExtendedJButton btn;
    private JButton btnOK;

    private UserInfo selectedWaiter = null;
    private ListProvider lpr;

    public JWaiterSelector(DataLogicSystem dlSystem) {
        super(new JFrame());
        waiterSelectorPane(dlSystem);
        pack();
        int x = JRootFrame.PARENTFRAME.getX() + ((JRootFrame.PARENTFRAME.getWidth() - this.getWidth()) / 2);
        int y = JRootFrame.PARENTFRAME.getY() + this.getHeight()/2 ;
        setLocation(x, y);

    }

    protected void waiterSelectorPane(DataLogicSystem dlSystem) {
        lpr = new ListProviderCreator(dlSystem.getActiveWaiters());
        try {
            jListItems.setModel(new MyListData(lpr.loadData()));
        } catch (BasicException ex) {
            Logger.getLogger(JWaiterSelector.class.getName()).log(Level.SEVERE, null, ex);
        }

        createListPanel(400, 400);
        setButtonPanel(new Dimension(105, 35));
        btnOK.setEnabled(false);

        waiterFinder.add(listPane, " wrap, growy, wrap");
        waiterFinder.add(btnPanel, "  align right, wrap");
        waiterFinder.add(btnMainPanel, "span ,  align right");
        mainPanel.add(waiterFinder, "align center, wrap");

        setResizable(false);
        setModal(true);

        mainPanel.setBorder(BorderFactory.createLineBorder(CustomColour.getBorderColour(), 2));
        setTitle("Waiter Selector");
        getContentPane().add(mainPanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                selectedWaiter = null;
            }
        });
        setAlwaysOnTop(true);
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
                    selectedWaiter = (UserInfo) jListItems.getSelectedValue();
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

    private void setButtonPanel(Dimension dimension) {
        btnPanel = new JPanel();

        btnMainPanel = new JPanel();

        btnOK = new ExtendedJButton(AppLocal.getIntString("button.ok"), JAlertPane.OK);
        btnOK.setPreferredSize(dimension);
        btnOK.setFont(btnFont);
        btnOK.setFocusable(false);
        btnOK.addActionListener((ActionEvent e) -> {
            selectedWaiter = (UserInfo) jListItems.getSelectedValue();
            dispose();
        });
        btnMainPanel.add(btnOK);

        btn = new ExtendedJButton(AppLocal.getIntString("button.cancel"), JAlertPane.CANCEL);
        btn.setPreferredSize(dimension);
        btn.setFont(btnFont);
        btn.setFocusable(false);
        btn.addActionListener((ActionEvent e) -> {
            selectedWaiter = null;
            dispose();
        });
        btnMainPanel.add(btn);
    }

    public UserInfo getSelectedWaiter() {
        return selectedWaiter;
    }
}
