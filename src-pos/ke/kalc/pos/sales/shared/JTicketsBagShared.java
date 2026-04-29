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
package ke.kalc.pos.sales.shared;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import ke.kalc.globals.SystemProperty;
import ke.kalc.basic.BasicException;
import ke.kalc.commons.dialogs.JAlertPane;
import ke.kalc.data.gui.MessageInf;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.AppView;
import ke.kalc.pos.datalogic.DataLogicSales;
import ke.kalc.pos.datalogic.DataLogicSystem;
import ke.kalc.pos.datalogic.DataLogicLoyalty;
import ke.kalc.pos.loyalty.LoyaltyCard;
import ke.kalc.pos.printer.DeviceDisplayAdvance;
import ke.kalc.pos.printer.TicketParser;
import ke.kalc.pos.printer.TicketPrinterException;
import ke.kalc.pos.datalogic.DataLogicReceipts;
import ke.kalc.pos.sales.JPanelTicket;
import ke.kalc.pos.sales.JTicketsBag;
import ke.kalc.pos.sales.SharedTicketInfo;
import ke.kalc.pos.sales.TicketsEditor;
import ke.kalc.pos.scripting.ScriptEngine;
import ke.kalc.pos.scripting.ScriptException;
import ke.kalc.pos.scripting.ScriptFactory;
import ke.kalc.pos.ticket.TicketInfo;
import ke.kalc.pos.ticket.TicketLineInfo;
import ke.kalc.pos.ticket.UserInfo;
import ke.kalc.pos.util.AutoLogoff;
import ke.kalc.globals.IconFactory;
import ke.kalc.pos.auditing.Audit;
import ke.kalc.pos.forms.AppUser;
import ke.kalc.pos.forms.KALCFonts;
import ke.kalc.pos.sales.CustomerDisplay;

/**
 *
 *
 */
public class JTicketsBagShared extends JTicketsBag {

    private String m_sCurrentTicket = null;
    private DataLogicReceipts dlReceipts = null;
    private final DataLogicSales dlSales;
    private final DataLogicSystem dlSystem;
    private final DataLogicLoyalty dlLoyalty;
    private StringBuilder pickupBarcode;
    private JPanelTicket panelTicket;

    /**
     * Creates new form JTicketsBagShared
     *
     * @param app
     * @param panelticket
     */
    public JTicketsBagShared(AppView app, TicketsEditor panelticket) {

        super(app, panelticket);
        this.panelTicket = (JPanelTicket) panelticket;

        dlReceipts = (DataLogicReceipts) app.getBean("ke.kalc.pos.datalogic.DataLogicReceipts");
        dlSales = (DataLogicSales) app.getBean("ke.kalc.pos.datalogic.DataLogicSales");
        dlSystem = (DataLogicSystem) app.getBean("ke.kalc.pos.datalogic.DataLogicSystem");
        dlLoyalty = new DataLogicLoyalty();

        initComponents();
        checkLayaways();

        m_jListTickets.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent pressedEvent) {
                if (pressedEvent.getButton() == MouseEvent.BUTTON3) {
                    deleteAllShared();
                }
            }
        });

        m_jDelTicket.addActionListener((ActionEvent e) -> {
            if (JAlertPane.messageBox(JAlertPane.WARNING, AppLocal.getIntString("message.wannadelete"), 16,
                    new Dimension(125, 50), JAlertPane.YES_NO_OPTION) == 5) {
                Boolean remote = false;
                for (TicketLineInfo line : m_panelticket.getActiveTicket().getLines()) {
                    Audit.itemRemoved(m_panelticket.getActiveTicket(), line, "Full Ticket Removed");
                    if ("OK".equals(line.getProperty("sendstatus"))) {
                        line.setProperty("sendstatus", "Cancel");
                        remote = true;
                    }
                }
                
                if (remote) {
                    panelTicket.printTicket("Printer.TicketKitchen", m_panelticket.getActiveTicket(), null, true);
                    JAlertPane.messageBox(JAlertPane.INFORMATION, AppLocal.getIntString("message.orderCancellation"), 16, new Dimension(125, 50), JAlertPane.OK_OPTION);
                }
                
                m_sCurrentTicket = null;
                CustomerDisplay.updateDisplay("display.Message");
                newTicket();
            }
        });
    }

    @Override
    public void deleteAllShared() {
        AutoLogoff.getInstance().deactivateTimer();
        if (JAlertPane.messageBox(JAlertPane.WARNING, AppLocal.getIntString("message.deleteallshared"), 16,
                new Dimension(125, 50), JAlertPane.YES_NO_OPTION) == 5) {
            try {
                if (AppUser.hasPermission("access.alltickets")) {
                    //get shared tickets list
                    for (SharedTicketInfo aticket : dlReceipts.getSharedTicketList()) {
                        TicketInfo ticket = dlReceipts.getSharedTicket(aticket.getId());
                        deleteSharedTicket(ticket);
                    }
                    dlReceipts.deleteAllSharedTickets();
                } else if (SystemProperty.SHAREDTICKETBYUSER) {
                    for (SharedTicketInfo aticket : dlReceipts.getSharedTicketListByUser(m_App.getAppUserView().getUser().getName())) {
                        TicketInfo ticket = dlReceipts.getSharedTicket(aticket.getId());
                        deleteSharedTicket(ticket);
                    }
                    dlReceipts.deleteSharedTickets(m_App.getAppUserView().getUser().getName());
                } else {
                    for (SharedTicketInfo aticket : dlReceipts.getSharedTicketList()) {
                        TicketInfo ticket = dlReceipts.getSharedTicket(aticket.getId());
                        deleteSharedTicket(ticket);
                    }
                    dlReceipts.deleteAllSharedTickets();
                }
                m_jListTickets.setText("");
                m_jListTickets.setIcon(IconFactory.getIcon("sale_pending.png"));
                m_jListTickets.setEnabled(false);
            } catch (BasicException ex) {
                Logger.getLogger(JTicketsBagShared.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        AutoLogoff.getInstance().activateTimer();
    }

    public void deleteSharedTicket(TicketInfo ticket) {
        Boolean remote = false;
        for (TicketLineInfo line : ticket.getLines()) {
            Audit.itemRemoved(ticket, line, "Shared Ticket Removed");
            if ("OK".equals(line.getProperty("sendstatus"))) {
                line.setProperty("sendstatus", "Cancel");
                remote = true;
            }
        }
        if (remote) {
            panelTicket.printTicket("Printer.TicketKitchen", ticket, ticket.getPlace(), true);
            JAlertPane.messageBox(JAlertPane.INFORMATION, AppLocal.getIntString("message.orderCancellation"), 16, new Dimension(125, 50), JAlertPane.OK_OPTION);
        }
    }

    /**
     *
     */
    @Override
    public void activate() {
        m_sCurrentTicket = null;
        selectValidTicket();
        m_jDelTicket.setEnabled(AppUser.hasPermission("ke.kalc.pos.sales.JPanelTicketEdits"));
        checkLayaways();
    }

    /**
     *
     * @return
     */
    @Override
    public boolean deactivate() {
        saveCurrentTicket();

        m_sCurrentTicket = null;
        m_panelticket.setActiveTicket(null, null);

        return true;

    }

    /**
     *
     */
    @Override
    public void deleteTicket() {
        m_sCurrentTicket = null;
        newTicket();
    }

    private void updateCustomerDisplay(String sresourcename) {
        String source = dlSystem.getResourceAsXML(sresourcename);
        try {
            if (!m_App.getDeviceTicket().getDeviceDisplay().getDisplayName().equals("Display not available")) {
                DeviceDisplayAdvance advDisplay = (DeviceDisplayAdvance) m_App.getDeviceTicket().getDeviceDisplay();
                advDisplay.setProductImage(null);
            }
            ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
            new TicketParser(m_App.getDeviceTicket(), dlSystem).printTicket(script.eval(source).toString());
        } catch (ScriptException | TicketPrinterException ex) {
            Logger.getLogger(JTicketsBagShared.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     *
     * @return
     */
    @Override
    protected JComponent getBagComponent() {
        return this;
    }

    /**
     *
     * @return
     */
    @Override
    protected JComponent getNullComponent() {
        return new JPanel();
    }

    private void saveCurrentTicket() {
        if (m_sCurrentTicket != null) {
            try {
                if (SystemProperty.USEPICKUPFORLAYAWAY) {
                    // test if ticket as pickupid snd Only assign a pickupid if ticket has an article count
                    if ((m_panelticket.getActiveTicket().getPickupId() == 0)
                            && (m_panelticket.getActiveTicket().getArticlesCount() > 0)) {
                        m_panelticket.getActiveTicket().setPickupId(dlSales.getNextPickupIndex());
                    }
                    m_panelticket.getActiveTicket().setSharedTicket(Boolean.TRUE);
                    dlReceipts.insertSharedTicketUsingPickUpID(m_sCurrentTicket,
                            m_panelticket.getActiveTicket(),
                            m_panelticket.getActiveTicket().getPickupId(),
                            "P" + m_panelticket.getActiveTicket().getId().substring(24),
                            m_panelticket.getActiveTicket().fetchDeliveryInfo());
                } else {
                    m_panelticket.getActiveTicket().setSharedTicket(Boolean.TRUE);
                    dlReceipts.insertSharedTicket(m_sCurrentTicket,
                            m_panelticket.getActiveTicket(),
                            m_panelticket.getActiveTicket().getPickupId(),
                            "P" + m_panelticket.getActiveTicket().getId().substring(24),
                            m_panelticket.getActiveTicket().fetchDeliveryInfo()
                    );
                }

                TicketInfo l = dlReceipts.getSharedTicket(m_sCurrentTicket);
                if (l.getLinesCount() == 0) {
                    dlReceipts.deleteSharedTicket(m_sCurrentTicket);
                }
                checkLayaways();
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }
        }
    }

    private void setActiveTicket(String id) throws BasicException {
        // BEGIN TRANSACTION
        TicketInfo ticket = dlReceipts.getSharedTicket(id);
        if (ticket == null) {
            m_jListTickets.setText("");
            throw new BasicException(AppLocal.getIntString("message.noticket"));
        } else {
            Date tmpDate = ticket.getdDate();
            UserInfo tmpUser = ticket.getSharedTicketUser();
            Integer pickUp = dlReceipts.getPickupId(id);
            String tName = dlReceipts.getTicketName(id);
            String loyalty = dlReceipts.getLoyaltyCard(id);
            ticket.setDeliveryInfo(dlReceipts.fetchDeliveryInfo(id));

            //Valid points if loyalty card if not enough points remove item
            if (loyalty != null) {
                Integer points = DataLogicLoyalty.getBalanceByCardNumber(loyalty);
                for (int count = ticket.getLines().size() - 1; count >= 0; count--) {
                    if (ticket.getLines().get(count).getProperty("redeemed_points") != null) {
                        if (points >= Integer.valueOf(ticket.getLines().get(count).getProperty("redeemed_points"))) {
                            points = points - Integer.valueOf(ticket.getLines().get(count).getProperty("redeemed_points"));
                        } else {
                            ticket.getLines().remove(count);
                        }
                    }
                }
            }

            dlReceipts.deleteSharedTicket(id);
            m_sCurrentTicket = id;
            if (loyalty != null) {
                ticket.setLoyaltyCard(new LoyaltyCard(loyalty));
            }
            m_panelticket.setActiveTicket(ticket, null);
            ticket.setPickupId(pickUp);
            ticket.setdDate(tmpDate);
            ticket.setUser(tmpUser);

            m_panelticket.setTicketName(tName);
        }
        checkLayaways();
    }

    //private CustomerDeliveryInfo deliveryInfo;
    private void checkLayaways() {
        List<SharedTicketInfo> nl;
        try {
            if (AppUser.hasPermission("access.alltickets")) {
                nl = dlReceipts.getSharedTicketList();
            } else if (SystemProperty.SHAREDTICKETBYUSER) {
                m_jListTickets.setText("");
                m_jListTickets.setIcon(IconFactory.getIcon("sale_pending.png"));
                m_jListTickets.setEnabled(false);
                nl = dlReceipts.getSharedTicketListByUser(m_App.getAppUserView().getUser().getName());
            } else {
                nl = dlReceipts.getSharedTicketList();
            }

            if (nl.isEmpty()) {
                m_jListTickets.setText("");
                m_jListTickets.setIcon(IconFactory.getIcon("sale_pending.png"));
                m_jListTickets.setEnabled(false);
            } else {
                m_jListTickets.setText("" + Integer.toString(nl.size()));
                m_jListTickets.setIcon(IconFactory.getIcon("sales_active.png"));
                m_jListTickets.setEnabled(true);
            }
        } catch (BasicException e) {
        }
    }

    private void selectValidTicket() {
        newTicket();
        if (SystemProperty.LAYAWAYPOPUP) {
            try {
                List<SharedTicketInfo> l = dlReceipts.getSharedTicketList();
                if (l.isEmpty()) {
                    m_jListTickets.setText("");
                    newTicket();
                    AutoLogoff.getInstance().activateTimer();
                } else {
                    m_jListTicketsActionPerformed(null);
                }
            } catch (BasicException e) {
                new MessageInf(e).show(this);
                newTicket();
                AutoLogoff.getInstance().activateTimer();
            }
        }
    }

    public void newTicket() {
        saveCurrentTicket();
        pickupBarcode = new StringBuilder();
        TicketInfo ticket = new TicketInfo();
        ticket.setTicketOwner(m_App.getAppUserView().getUser().getId());
        m_sCurrentTicket = UUID.randomUUID().toString();
        m_panelticket.setActiveTicket(ticket, null);
        pickupBarcode.append("P").append(ticket.getId().substring(24));
    }

    @Override
    public void getTicketByCode(String id) {
        saveCurrentTicket();

        List<SharedTicketInfo> l = null;
        try {
            l = dlReceipts.getSharedTicketList();
        } catch (BasicException e) {
        }
        for (SharedTicketInfo t : l) {
            if (t.getPickupBarcode().equals(id)) {
                saveCurrentTicket();
                try {
                    setActiveTicket(t.getId());
                } catch (BasicException ex) {
                    Logger.getLogger(JTicketsBagShared.class.getName()).log(Level.SEVERE, null, ex);
                }
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

        jPanel1 = new javax.swing.JPanel();
        m_jNewTicket = new javax.swing.JButton();
        m_jDelTicket = new javax.swing.JButton();
        m_jListTickets = new javax.swing.JButton();

        setFont(KALCFonts.DEFAULTFONT.deriveFont(14f));
        setLayout(new java.awt.BorderLayout());

        m_jNewTicket.setIcon(IconFactory.getIcon("sale_new.png"));
        m_jNewTicket.setFocusPainted(false);
        m_jNewTicket.setFocusable(false);
        m_jNewTicket.setMargin(new java.awt.Insets(0, 4, 0, 4));
        m_jNewTicket.setMaximumSize(new java.awt.Dimension(50, 40));
        m_jNewTicket.setMinimumSize(new java.awt.Dimension(50, 40));
        m_jNewTicket.setPreferredSize(new java.awt.Dimension(52, 40));
        m_jNewTicket.setRequestFocusEnabled(false);
        m_jNewTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jNewTicketActionPerformed(evt);
            }
        });
        jPanel1.add(m_jNewTicket);

        m_jDelTicket.setIcon(IconFactory.getIcon("sale_delete.png"));
        m_jDelTicket.setFocusPainted(false);
        m_jDelTicket.setFocusable(false);
        m_jDelTicket.setMargin(new java.awt.Insets(0, 4, 0, 4));
        m_jDelTicket.setMaximumSize(new java.awt.Dimension(50, 40));
        m_jDelTicket.setMinimumSize(new java.awt.Dimension(50, 40));
        m_jDelTicket.setPreferredSize(new java.awt.Dimension(52, 40));
        m_jDelTicket.setRequestFocusEnabled(false);
        jPanel1.add(m_jDelTicket);

        m_jListTickets.setFont(KALCFonts.DEFAULTFONTBOLD);
        m_jListTickets.setIcon(IconFactory.getIcon("sale_pending.png"));
        m_jListTickets.setText("99");
        m_jListTickets.setFocusPainted(false);
        m_jListTickets.setFocusable(false);
        m_jListTickets.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_jListTickets.setIconTextGap(-3);
        m_jListTickets.setMargin(new java.awt.Insets(0, 4, 0, 4));
        m_jListTickets.setMaximumSize(new java.awt.Dimension(55, 40));
        m_jListTickets.setMinimumSize(new java.awt.Dimension(55, 40));
        m_jListTickets.setPreferredSize(new java.awt.Dimension(52, 40));
        m_jListTickets.setRequestFocusEnabled(false);
        m_jListTickets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jListTicketsActionPerformed(evt);
            }
        });
        jPanel1.add(m_jListTickets);

        add(jPanel1, java.awt.BorderLayout.WEST);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jListTicketsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jListTicketsActionPerformed

        SwingUtilities.invokeLater(() -> {
            List<SharedTicketInfo> l;
            try {
                if (m_App.getAppUserView().getUser().hasPermission("access.alltickets")) {
                    l = dlReceipts.getSharedTicketList();
                } else if (SystemProperty.SHAREDTICKETBYUSER) {
                    l = dlReceipts.getSharedTicketListByUser(m_App.getAppUserView().getUser().getName());
                } else {
                    l = dlReceipts.getSharedTicketList();
                }
                
                JTicketsBagSharedList listDialog = JTicketsBagSharedList.newJDialog(JTicketsBagShared.this);
                
                String id = listDialog.showTicketsList(l);
                
                if (id != null) {
                    saveCurrentTicket();
                    m_sCurrentTicket = id;
                    setActiveTicket(id);
                    
                }
            } catch (BasicException e) {
                new MessageInf(e).show(JTicketsBagShared.this);
                newTicket();
            }
        });
    }//GEN-LAST:event_m_jListTicketsActionPerformed

    public void selectLayaway() {
        m_jListTicketsActionPerformed(null);
    }

    public void delTicketAction() {
        AutoLogoff.getInstance().deactivateTimer();
        for (TicketLineInfo line : m_panelticket.getActiveTicket().getLines()) {
            if (("OK".equals(line.getProperty("sendstatus")) && !SystemProperty.ALLOWSENTITEMREFUND)) {
                JAlertPane.messageBox(JAlertPane.INFORMATION, AppLocal.getIntString("message.cannotdeletesentorder"), 16, new Dimension(125, 50), JAlertPane.OK_OPTION);
                return;
            }
        }

        if (JAlertPane.messageBox(JAlertPane.CONFIRMATION, AppLocal.getIntString("message.wannadelete"), 16, new Dimension(125, 50), JAlertPane.YES_NO_OPTION) == 5) {
            updateCustomerDisplay("display.Message");
            deleteTicket();
        }
        AutoLogoff.getInstance().activateTimer();
    }


    private void m_jNewTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jNewTicketActionPerformed
        newTicket();
    }//GEN-LAST:event_m_jNewTicketActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton m_jDelTicket;
    private javax.swing.JButton m_jListTickets;
    private javax.swing.JButton m_jNewTicket;
    // End of variables declaration//GEN-END:variables

}
