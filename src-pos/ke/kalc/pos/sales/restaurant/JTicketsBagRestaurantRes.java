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
package ke.kalc.pos.sales.restaurant;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Comparator;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import net.miginfocom.swing.MigLayout;
import ke.kalc.basic.BasicException;
import ke.kalc.beans.DateUtils;
import ke.kalc.beans.JCalendarPanel;
import ke.kalc.beans.JTimePanel;
import ke.kalc.commons.dialogs.JAlertPane;
import ke.kalc.custom.CustomColour;
import ke.kalc.custom.CustomJLabel;
import ke.kalc.custom.CustomJTextField;
import ke.kalc.custom.ExtendedJButton;
import ke.kalc.data.gui.MessageInf;
import ke.kalc.data.user.DirtyManager;
import ke.kalc.data.user.EditorCreator;
import ke.kalc.data.user.ListProvider;
import ke.kalc.data.user.ListProviderCreator;
import ke.kalc.format.Formats;
import ke.kalc.pos.customers.CustomerInfo;
import ke.kalc.pos.datalogic.DataLogicCustomers;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.AppView;
import ke.kalc.globals.IconFactory;
import ke.kalc.pos.forms.KALCFonts;

/**
 *
 *
 */
public class JTicketsBagRestaurantRes extends javax.swing.JPanel {

    private JTicketsBagRestaurantMap m_restaurantmap;
    private DataLogicCustomers dlCustomers = null;
    private DirtyManager m_Dirty;
    private Object m_sID;
    private CustomerInfo customer;
    private Date m_dCreated;
    private boolean m_bReceived;
    private Date m_dcurrentday;
    private JTimePanel m_timepanel = new JTimePanel();
    private Object[] avalue;

    private JCalendarPanel datePanel = new JCalendarPanel(false);
    private boolean m_bpaintlock = false;

    private final JPanel leftPanel = new JPanel(new MigLayout("insets 0 15 0 0 ", "[440:440:440]", "[280:280:280][30::]0[30::]"));
    private final JPanel rightPanel = new JPanel(new MigLayout("insets 0 15 0 0 ", "[]", "[200::][]"));

    private final JPanel btnPanelTop = new JPanel();
    private final JPanel btnPanelBottom = new JPanel();
    private ExtendedJButton btnArrived = null;
    private ExtendedJButton btnCancel = null;

    private final JPanel listPanel = new JPanel();
    private final JScrollPane jScrollPane1 = new JScrollPane();
    private final JList jListReservations = new JList();
    private ListProvider lpr;

    private final Font txtFont = KALCFonts.DEFAULTFONT.deriveFont(18f);
    private final Font btnFont = KALCFonts.DEFAULTBUTTONFONT;
    private final Font lblFont = KALCFonts.DEFAULTFONT.deriveFont(16f);

    private final CustomJTextField customerName = new CustomJTextField(new Dimension(225, 25), txtFont);
    private final CustomJTextField covers = new CustomJTextField(new Dimension(75, 25), txtFont);
    private final CustomJTextField resevationTime = new CustomJTextField(new Dimension(75, 25), txtFont);
    private final JTextArea comments = new JTextArea(1, 1);

    /**
     * Creates new form JPanelReservations
     *
     * @param oApp
     * @param restaurantmap
     */
    public JTicketsBagRestaurantRes(AppView oApp, JTicketsBagRestaurantMap restaurantmap) {

        m_restaurantmap = restaurantmap;
        dlCustomers = (DataLogicCustomers) oApp.getBean("ke.kalc.pos.datalogic.DataLogicCustomers");

        customerName.setEditable(false);
        covers.setEditable(false);
        comments.setEditable(false);
        resevationTime.setEditable(false);

        m_dcurrentday = new Date();

        initComponents();

        createReservationsPanel();
        
       

        btnArrived.setEnabled(jListReservations.getSelectedValue() != null);
        btnCancel.setEnabled(jListReservations.getSelectedValue() != null);

        m_Dirty = new DirtyManager();

    }

    private void createReservationsPanel() {
        setButtonPanel(new Dimension(225, 35));

        //Add the calender panel
        JPanel calenderPanel = new JPanel();
        calenderPanel.add(datePanel);
        datePanel.addPropertyChangeListener("Date", new DateChangeCalendarListener());
        leftPanel.add(calenderPanel, "wrap");

        //add the reservation list
        createListPanel(480, 450);

        //Add customer resevation details
        JPanel resevationDetailsPanel = new JPanel(new MigLayout("insets 0 0 0 10 ", "[100::]10[]", "[][][][][]"));

        comments.setText("");
        comments.setFont(txtFont);
        comments.setLineWrap(true);
        comments.setWrapStyleWord(true);
        comments.setBorder(BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128), 1));
        comments.setPreferredSize(new Dimension(400, 90));

        resevationDetailsPanel.add(new CustomJLabel(AppLocal.getIntString("label.customer"), lblFont));
        resevationDetailsPanel.add(customerName, "growx, wrap");
        resevationDetailsPanel.add(new CustomJLabel(AppLocal.getIntString("label.covers"), lblFont));
        resevationDetailsPanel.add(covers, "wrap");
        resevationDetailsPanel.add(new CustomJLabel(AppLocal.getIntString("label.time"), lblFont));
        resevationDetailsPanel.add(resevationTime, " wrap");
        resevationDetailsPanel.add(new CustomJLabel(AppLocal.getIntString("label.notes"), lblFont));
        resevationDetailsPanel.add(comments, "span, wrap");
        leftPanel.add(resevationDetailsPanel, "gaptop 5, wrap");

        leftPanel.add(btnPanelTop, "gaptop 25, gapbottom 2, align right, wrap");
        leftPanel.add(btnPanelBottom, "gaptop 2, align right, wrap");

        m_timepanel = new JTimePanel(null, JTimePanel.BUTTONS_HOUR);
        m_timepanel.setPeriod(10800000L);
        m_timepanel.setPreferredSize(new Dimension(310, 190));
        m_timepanel.addPropertyChangeListener("Date", new DateChangeTimeListener());

        rightPanel.add(m_timepanel, "gaptop 15, span, wrap");
        rightPanel.add(listPanel, "wrap, growy");

        basePanel.add(leftPanel);
        basePanel1.add(rightPanel);
    }

    private void setTextAreaParameters(JTextArea textArea) {
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setDisabledTextColor(CustomColour.getEnabledColour(textArea.getBackground()));
        textArea.setEnabled(false);
        textArea.setFocusable(false);
        textArea.setOpaque(false);
        textArea.setRequestFocusEnabled(false);
    }

    private void setButtonPanel(Dimension dimension) {
        btnArrived = new ExtendedJButton(AppLocal.getIntString("button.customerarrived"), JAlertPane.OK);
        btnArrived.setPreferredSize(dimension);
        btnArrived.setFont(btnFont);
        btnArrived.addActionListener((ActionEvent e) -> {
            ExtendedJButton extBtn = (ExtendedJButton) e.getSource();
            customerArrived((String) avalue[0]);
        });
        btnPanelTop.add(btnArrived);

        btnCancel = new ExtendedJButton(AppLocal.getIntString("button.cancelreservation"), JAlertPane.CANCEL);
        btnCancel.setPreferredSize(dimension);
        btnCancel.setFont(btnFont);
        btnCancel.addActionListener((ActionEvent e) -> {
            ExtendedJButton extBtn = (ExtendedJButton) e.getSource();
            cancelReservation((String) avalue[0]);
        });
        btnPanelBottom.add(btnCancel);
    }

    private void createListPanel(Integer width, Integer height) {
        lpr = new ListProviderCreator(dlCustomers.getReservationsList(), new MyDateFilter());
  
        try {
            jListReservations.setModel(new MyListData(lpr.loadData()));
        } catch (BasicException ex) {
            System.out.println("");
        }

        listPanel.setPreferredSize(new Dimension(width, height));
        listPanel.setLayout(new java.awt.BorderLayout());
        listPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jListReservations.setCellRenderer(new JCalendarItemRenderer());
        jListReservations.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(20f));
        jListReservations.setFixedCellHeight(40);
        jListReservations.setFocusable(false);
        jListReservations.setRequestFocusEnabled(false);
        jListReservations.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    customer = new CustomerInfo(null);
                    customer.setTaxid(null);
                    customer.setName(customerName.getText());
                    customerArrived((String) avalue[0]);
                }
            }
        });

        jListReservations.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                btnArrived.setEnabled(jListReservations.getSelectedValue() != null);
                btnCancel.setEnabled(jListReservations.getSelectedValue() != null);

                if (jListReservations.getSelectedValue() == null) {
                    avalue = null;
                    customerName.setText("");
                    covers.setText("");
                    comments.setText("");
                    resevationTime.setText("");
                } else {
                    customer = new CustomerInfo(null);
                    customer.setTaxid(null);
                    customer.setName(customerName.getText());
                    avalue = (Object[]) jListReservations.getSelectedValue();
                    resevationTime.setText(Formats.HOURMIN.formatValue(avalue[2]));
                    customerName.setText(Formats.STRING.formatValue(avalue[5]));
                    covers.setText(Formats.INT.formatValue(avalue[6]));
                    //    m_bDone = ((Boolean) avalue[7]);
                    comments.setText(Formats.STRING.formatValue(avalue[8]));
                    if ((Boolean) avalue[7]) {
                        btnArrived.setEnabled(false);
                        btnCancel.setEnabled(false);
                    }
                }

            }
        });
        jScrollPane1.getVerticalScrollBar().setPreferredSize(new Dimension(35, 35));
        jScrollPane1.setViewportView(jListReservations);

    }

    private void cancelReservation(String id) {
        m_Dirty.setDirty(true);

        if (JAlertPane.messageBox(JAlertPane.WARNING, AppLocal.getIntString("message.deletereservation"), 16,
                new Dimension(125, 50), JAlertPane.YES_NO_OPTION) == 6) {
            return;
        }

        try {
            //save the record
            dlCustomers.getReservationsDelete().exec(new Object[]{id});
            lpr = new ListProviderCreator(dlCustomers.getReservationsList(), new MyDateFilter());
            try {
                jListReservations.setModel(new MyListData(lpr.loadData()));
            } catch (BasicException ex) {
                System.out.println("");
            }
            btnArrived.setEnabled(false);
            btnCancel.setEnabled(false);
        } catch (BasicException ex) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.nosaveticket"), ex);
            msg.show(this);
        }
    }

    private void customerArrived(String id) {
        if (customer.getName() == null) {
            return;
        }

        m_bReceived = true;
        m_Dirty.setDirty(true);

        try {
            dlCustomers.receiveCustomer(id);
            btnArrived.setEnabled(false);
            btnCancel.setEnabled(false);
            m_restaurantmap.viewTables(customer);
        } catch (BasicException ex) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.nosaveticket"), ex);
            msg.show(this);
        }
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

    private class MyDateFilter implements EditorCreator {

        @Override
        public Object createValue() throws BasicException {
            return new Object[]{m_dcurrentday, new Date(m_dcurrentday.getTime() + 10800000L)};
        }
    }

    public void activate() {
        reload(DateUtils.getTodayHours(new Date()));
    }

    public void refresh() {
    }

    public boolean deactivate() {
        return true;

    }

    private static class CompareReservations implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            Object[] a1 = (Object[]) o1;
            Object[] a2 = (Object[]) o2;
            Date d1 = (Date) a1[2];
            Date d2 = (Date) a2[2];
            int c = d1.compareTo(d2);
            if (c == 0) {
                d1 = (Date) a1[1];
                d2 = (Date) a2[1];
                return d1.compareTo(d2);
            } else {
                return c;
            }
        }
    }

    private void reload(Date dDate) {
        if (!dDate.equals(m_dcurrentday)) {
            Date doldcurrentday = m_dcurrentday;
            m_dcurrentday = dDate;
            lpr = new ListProviderCreator(dlCustomers.getReservationsList(), new MyDateFilter());
        }
        try {
            jListReservations.setModel(new MyListData(lpr.loadData()));
        } catch (BasicException ex) {
            System.out.println("");
        }
        paintDate();
    }

    private void paintDate() {
        m_bpaintlock = true;
        datePanel.setDate(m_dcurrentday);
        m_timepanel.setDate(m_dcurrentday);
        m_bpaintlock = false;

    }

    private class DateChangeCalendarListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (!m_bpaintlock) {
                reload(DateUtils.getTodayHours(DateUtils.getDate(datePanel.getDate(), m_timepanel.getDate())));
            }
        }
    }

    private class DateChangeTimeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (!m_bpaintlock) {
                reload(DateUtils.getTodayHours(DateUtils.getDate(datePanel.getDate(), m_timepanel.getDate())));
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

        jPanel2 = new javax.swing.JPanel();
        m_jButtonContainer = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        m_jbtnTables = new javax.swing.JButton();
        m_jbtnAddReservation = new javax.swing.JButton();
        basePanel = new javax.swing.JPanel();
        basePanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        m_jButtonContainer.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        m_jbtnTables.setFont(KALCFonts.DEFAULTBUTTONFONT);
        m_jbtnTables.setIcon(IconFactory.getIcon("tables.png"));
        m_jbtnTables.setText(AppLocal.getIntString("button.tables")); // NOI18N
        m_jbtnTables.setFocusPainted(false);
        m_jbtnTables.setFocusable(false);
        m_jbtnTables.setMaximumSize(new java.awt.Dimension(125, 42));
        m_jbtnTables.setMinimumSize(new java.awt.Dimension(125, 42));
        m_jbtnTables.setPreferredSize(new java.awt.Dimension(125, 42));
        m_jbtnTables.setRequestFocusEnabled(false);
        m_jbtnTables.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnTablesActionPerformed(evt);
            }
        });
        jPanel4.add(m_jbtnTables);

        m_jbtnAddReservation.setFont(KALCFonts.DEFAULTBUTTONFONT);
        m_jbtnAddReservation.setIcon(IconFactory.getIcon("stockdiary.png"));
        m_jbtnAddReservation.setText(AppLocal.getIntString("button.newbooking")); // NOI18N
        m_jbtnAddReservation.setFocusPainted(false);
        m_jbtnAddReservation.setFocusable(false);
        m_jbtnAddReservation.setMaximumSize(new java.awt.Dimension(200, 40));
        m_jbtnAddReservation.setMinimumSize(new java.awt.Dimension(150, 40));
        m_jbtnAddReservation.setPreferredSize(new java.awt.Dimension(200, 40));
        m_jbtnAddReservation.setRequestFocusEnabled(false);
        m_jbtnAddReservation.setRolloverEnabled(false);
        m_jbtnAddReservation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnAddReservationActionPerformed(evt);
            }
        });
        jPanel4.add(m_jbtnAddReservation);

        m_jButtonContainer.add(jPanel4, java.awt.BorderLayout.LINE_START);

        jPanel2.add(m_jButtonContainer, java.awt.BorderLayout.NORTH);

        basePanel.setFont(KALCFonts.DEFAULTFONT.deriveFont(13f)
        );
        basePanel.setMaximumSize(new java.awt.Dimension(32767, 440));
        basePanel.setMinimumSize(new java.awt.Dimension(100, 100));
        basePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 5));
        jPanel2.add(basePanel, java.awt.BorderLayout.WEST);

        basePanel1.setFont(KALCFonts.DEFAULTFONT.deriveFont(13f)
        );
        basePanel1.setMinimumSize(new java.awt.Dimension(100, 100));
        basePanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 5));
        jPanel2.add(basePanel1, java.awt.BorderLayout.CENTER);

        add(jPanel2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jbtnTablesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnTablesActionPerformed

        m_restaurantmap.viewTables();

    }//GEN-LAST:event_m_jbtnTablesActionPerformed

    private void m_jbtnAddReservationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnAddReservationActionPerformed
        // Create a new booking form
        CustomerReservations reservation = new CustomerReservations(dlCustomers);
        reservation.setVisible(true);

        lpr = new ListProviderCreator(dlCustomers.getReservationsList(), new MyDateFilter());
        try {
            jListReservations.setModel(new MyListData(lpr.loadData()));
        } catch (BasicException ex) {
            System.out.println("");
        }

    }//GEN-LAST:event_m_jbtnAddReservationActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel basePanel;
    private javax.swing.JPanel basePanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel m_jButtonContainer;
    private javax.swing.JButton m_jbtnAddReservation;
    private javax.swing.JButton m_jbtnTables;
    // End of variables declaration//GEN-END:variables

}
