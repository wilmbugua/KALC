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
package uk.kalc.pos.sales.restaurant;

import bsh.Interpreter;
import java.awt.Dimension;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.kalc.commons.dialogs.JAlertPane;
import uk.kalc.data.gui.JMessageDialog;
import uk.kalc.data.gui.ListKeyed;
import uk.kalc.data.gui.MessageInf;
import uk.kalc.data.loader.SentenceList;
import uk.kalc.pos.forms.AppLocal;
import uk.kalc.pos.forms.AppView;
import uk.kalc.pos.datalogic.DataLogicSystem;
import uk.kalc.pos.printer.DeviceDisplayAdvance;
import uk.kalc.pos.printer.DeviceTicket;
import uk.kalc.pos.printer.IncludeFile;
import uk.kalc.pos.printer.TicketParser;
import uk.kalc.pos.printer.TicketPrinterException;
import uk.kalc.pos.sales.TaxesLogic;
import uk.kalc.pos.sales.shared.JTicketsBagShared;
import uk.kalc.pos.scripting.ScriptEngine;
import uk.kalc.pos.scripting.ScriptException;
import uk.kalc.pos.scripting.ScriptFactory;
import uk.kalc.pos.ticket.TicketInfo;
import uk.kalc.pos.ticket.TicketLineInfo;
import uk.kalc.globals.IconFactory;
import uk.kalc.pos.forms.KALCFonts;
import uk.kalc.pos.sales.JPanelTicket;
import uk.kalc.pos.sales.TicketsEditor;

/**
 *
 *
 */
public class JTicketsBagRestaurant extends javax.swing.JPanel {

    private final AppView m_App;
    private final JTicketsBagRestaurantMap m_restaurant;
    private List<TicketLineInfo> m_aLines;
    private TicketLineInfo line;
    private TicketInfo ticket;
    private final Object ticketExt;
    private DataLogicSystem m_dlSystem = null;
    private final DeviceTicket m_TP;
    private final TicketParser m_TTP2;
    private final RestaurantDBUtils restDB;

    private final DataLogicSystem dlSystem = null;
    private TicketParser m_TTP;

    private SentenceList senttax;
    private ListKeyed taxcollection;
    private TaxesLogic taxeslogic;

    private Interpreter i;

    /**
     * Creates new form JTicketsBagRestaurantMap
     *
     * @param app
     * @param restaurant
     */
    public JTicketsBagRestaurant(AppView app, JTicketsBagRestaurantMap restaurant) {
        super();
        m_App = app;
        m_restaurant = restaurant;
        initComponents();
        ticketExt = null;

        restDB = new RestaurantDBUtils();

        m_dlSystem = (DataLogicSystem) m_App.getBean("uk.kalc.pos.datalogic.DataLogicSystem");
        m_TP = new DeviceTicket(app.getProperties());

        m_TTP2 = new TicketParser(m_App.getDeviceTicket(), m_dlSystem);
        //     m_KitchenPrint.setVisible(m_App.getAppUserView().getUser().hasPermission("sales.PrintKitchen"));
        //  m_KitchenPrint.setVisible(true);

    }

    /**
     *
     */
    public void activate() {

        // Authorization
        m_DelTicket.setEnabled(m_App.getAppUserView().getUser().hasPermission("uk.kalc.pos.sales.JPanelTicketEdits"));

    }

    /**
     *
     * @param resource
     */
    public void printTicket(String resource) {
        printTicket(resource, ticket, m_restaurant.getTable());
    }

    private void printTicket(String sresourcename, TicketInfo ticket, String table) {
        if (ticket != null) {
            String source = dlSystem.getResourceAsXML(sresourcename);
            String sresource;
            IncludeFile incFile = new IncludeFile(source, dlSystem);

            if (source == null) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"));
            } else {
                sresource = incFile.processInclude();
                try {
                    ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                    script.put("ticket", ticket);
                    script.put("place", m_restaurant.getTableName());
                    m_TTP2.printTicket(script.eval(sresource).toString());
                    //m_TTP2.printTicket(script.eval(m_dlSystem.getResourceAsXML(sresourcename)).toString());
                } catch (ScriptException | TicketPrinterException e) {
                    JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotprint"), e));
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

        m_DelTicket = new javax.swing.JButton();
        m_MoveTable = new javax.swing.JButton();
        m_TablePlan = new javax.swing.JButton();
        m_ChangeWaiter = new javax.swing.JButton();

        setFont(KALCFonts.DEFAULTFONT.deriveFont(14f));
        setMinimumSize(new java.awt.Dimension(250, 50));
        setPreferredSize(new java.awt.Dimension(250, 50));
        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        m_DelTicket.setIcon(IconFactory.getIcon("sale_delete.png"));
        m_DelTicket.setFocusPainted(false);
        m_DelTicket.setFocusable(false);
        m_DelTicket.setMargin(new java.awt.Insets(0, 4, 0, 4));
        m_DelTicket.setMaximumSize(new java.awt.Dimension(50, 40));
        m_DelTicket.setMinimumSize(new java.awt.Dimension(50, 40));
        m_DelTicket.setPreferredSize(new java.awt.Dimension(52, 40));
        m_DelTicket.setRequestFocusEnabled(false);
        m_DelTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_DelTicketActionPerformed(evt);
            }
        });
        add(m_DelTicket);

        m_MoveTable.setIcon(IconFactory.getIcon("movetable.png"));
        m_MoveTable.setFocusPainted(false);
        m_MoveTable.setFocusable(false);
        m_MoveTable.setMargin(new java.awt.Insets(0, 4, 0, 4));
        m_MoveTable.setMaximumSize(new java.awt.Dimension(50, 40));
        m_MoveTable.setMinimumSize(new java.awt.Dimension(50, 40));
        m_MoveTable.setPreferredSize(new java.awt.Dimension(52, 40));
        m_MoveTable.setRequestFocusEnabled(false);
        m_MoveTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_MoveTableActionPerformed(evt);
            }
        });
        add(m_MoveTable);

        m_TablePlan.setIcon(IconFactory.getIcon("tables.png"));
        m_TablePlan.setFocusPainted(false);
        m_TablePlan.setFocusable(false);
        m_TablePlan.setMargin(new java.awt.Insets(0, 4, 0, 4));
        m_TablePlan.setMaximumSize(new java.awt.Dimension(50, 40));
        m_TablePlan.setMinimumSize(new java.awt.Dimension(50, 40));
        m_TablePlan.setPreferredSize(new java.awt.Dimension(52, 40));
        m_TablePlan.setRequestFocusEnabled(false);
        m_TablePlan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_TablePlanActionPerformed(evt);
            }
        });
        add(m_TablePlan);

        m_ChangeWaiter.setIcon(IconFactory.getIcon("customer.png"));
        m_ChangeWaiter.setMaximumSize(new java.awt.Dimension(52, 40));
        m_ChangeWaiter.setMinimumSize(new java.awt.Dimension(52, 40));
        m_ChangeWaiter.setPreferredSize(new java.awt.Dimension(52, 40));
        m_ChangeWaiter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_ChangeWaiterActionPerformed(evt);
            }
        });
        add(m_ChangeWaiter);
    }// </editor-fold>//GEN-END:initComponents

    private void m_MoveTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_MoveTableActionPerformed
        restDB.clearCustomerNameInTableById(m_restaurant.getTable());
        restDB.clearWaiterNameInTableById(m_restaurant.getTable());
        restDB.clearTicketIdInTableById(m_restaurant.getTable());
        restDB.setTableMovedFlag(m_restaurant.getTable());
        restDB.clearTableLock(m_restaurant.getTable());
        m_restaurant.moveTicket();
    }//GEN-LAST:event_m_MoveTableActionPerformed

    @SuppressWarnings("empty-statement")
    private void m_DelTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_DelTicketActionPerformed
        if (JAlertPane.messageBox(JAlertPane.CONFIRMATION, AppLocal.getIntString("message.wannadelete"), 16, new Dimension(125, 50), JAlertPane.YES_NO_OPTION) == 5) {
            restDB.clearCustomerNameInTableById(m_restaurant.getTable());
            restDB.clearWaiterNameInTableById(m_restaurant.getTable());
            restDB.clearTicketIdInTableById(m_restaurant.getTable());
            restDB.clearTableLock(m_restaurant.getTable());
            m_restaurant.deleteFullTicket();

            try {
                if (!m_App.getDeviceTicket().getDeviceDisplay().getDisplayName().equals("Display not available")) {
                    DeviceDisplayAdvance advDisplay = (DeviceDisplayAdvance) m_App.getDeviceTicket().getDeviceDisplay();
                    advDisplay.setProductImage(null);
                }
                new TicketParser(m_App.getDeviceTicket(), m_dlSystem)
                        .printTicket(ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY)
                                .eval(m_dlSystem.getResourceAsXML("display.Message")).toString());
            } catch (ScriptException | TicketPrinterException ex) {
                Logger.getLogger(JTicketsBagShared.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }//GEN-LAST:event_m_DelTicketActionPerformed

    private void m_TablePlanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_TablePlanActionPerformed
        restDB.clearTableLock(m_restaurant.getTable());
        m_restaurant.newTicket();
    }//GEN-LAST:event_m_TablePlanActionPerformed

    @SuppressWarnings("empty-statement")
    private void m_ChangeWaiterActionPerformed(java.awt.event.ActionEvent evt) {

        JWaiterSelector finder = new JWaiterSelector(m_dlSystem);
        finder.setLocationRelativeTo(m_restaurant.getSalesPanel());
        finder.setVisible(true);

        if (finder.getSelectedWaiter() == null) {
            m_restaurant.getSalesPanel().doRefresh();
            return;
        }

        if (m_restaurant.getActiveTicket().getWaiter() != null) {
            if (m_restaurant.getActiveTicket().getWaiter().equalsIgnoreCase(finder.getSelectedWaiter().getId())) {
                int result = JAlertPane.messageBox(JAlertPane.WARNING, AppLocal.getIntString("dialog.changeWaiter"), 16,
                        new Dimension(125, 50), JAlertPane.YES_NO_OPTION);
                if (result == 6) {
                    m_restaurant.getSalesPanel().doRefresh();
                    return;
                }
            }
        }

        m_restaurant.getActiveTicket().setUser(finder.getSelectedWaiter());
        m_restaurant.getActiveTicket().setWaiter(finder.getSelectedWaiter().getId());
        restDB.setWaiterNameInTableByTicketId(finder.getSelectedWaiter().getName(), m_restaurant.getActiveTicket().getId());
        m_restaurant.getSalesPanel().doRefresh();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton m_ChangeWaiter;
    private javax.swing.JButton m_DelTicket;
    private javax.swing.JButton m_MoveTable;
    private javax.swing.JButton m_TablePlan;
    // End of variables declaration//GEN-END:variables

}
