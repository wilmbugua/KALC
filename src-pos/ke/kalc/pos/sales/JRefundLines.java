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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ke.kalc.basic.BasicException;
import ke.kalc.commons.dialogs.JAlertPane;
import ke.kalc.globals.SystemProperty;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.datalogic.DataLogicSales;
import ke.kalc.pos.datalogic.DataLogicSystem;
import ke.kalc.pos.forms.KALCFonts;
import ke.kalc.pos.ticket.TicketLineInfo;

/**
 *
 *
 */
public class JRefundLines extends javax.swing.JPanel {

    private final JTicketLines ticketlines;
    private static List m_aLines;
    private static DataLogicSales dlSales;
    private final JPanelTicketEdits m_jTicketEdit;
    private static TicketLineInfo tmpTicketInfo;
    private static Boolean serviceCharge;
    private static Boolean containsSC;
    private static Integer scLine;

    /**
     * Creates new form JRefundLines
     *
     * @param dlSystem
     * @param jTicketEdit
     */
    public JRefundLines(DataLogicSystem dlSystem, DataLogicSales dlsales, JPanelTicketEdits jTicketEdit) {
        dlSales = dlsales;
        m_jTicketEdit = jTicketEdit;

        initComponents();

        m_jbtnAddOne.setPreferredSize(new Dimension(150, 50));
        m_jbtnAddLine.setPreferredSize(new Dimension(150, 50));
        m_jbtnAddAll.setPreferredSize(new Dimension(150, 50));
        m_jbtnExclude.setPreferredSize(new Dimension(150, 50));
        m_jbtnExclude.setVisible(false);

        if (SystemProperty.TAXINCLUDED) {
            ticketlines = new JTicketLines(dlSystem.getResourceAsXML("Ticket.LineIncTaxes"));
        } else {
            ticketlines = new JTicketLines(dlSystem.getResourceAsXML("Ticket.LineExclTaxes"));
        }
        jPanel3.add(ticketlines, BorderLayout.CENTER);
    }

    /**
     *
     * @param aRefundLines
     */
    public void setLines(List aRefundLines) {
        serviceCharge = false;
        scLine = -1;
        containsSC = false;
        m_aLines = aRefundLines;
        ticketlines.clearTicketLines();
        int i = 0;
        if (m_aLines != null) {
            for (Object m_aLine : m_aLines) {
                TicketLineInfo temp = new TicketLineInfo();
                temp = (TicketLineInfo) m_aLine;

                if (temp.getProductID().equalsIgnoreCase("servicecharge")) {
                    containsSC = true;
                    scLine = i;
                }
                try {
                    temp.setProperty("salesticket", getPickupString(dlSales.getTicket(temp.getTicket())));
                } catch (BasicException ex) {
                    Logger.getLogger(JRefundLines.class.getName()).log(Level.SEVERE, null, ex);
                }
                ticketlines.addTicketLine(temp);
                i++;
            }
        }

        m_jbtnExclude.setVisible(containsSC && SystemProperty.EXCLUDESC);
    }

    public static void addBackLine(String name, Double qty, Double price, String line) {
        int i = 0;
        for (Object m_aLine : m_aLines) {
            tmpTicketInfo = ((TicketLineInfo) m_aLine);
            if (line.equals(tmpTicketInfo.getProperty("orgLine"))) {
                tmpTicketInfo.setRefundQty(tmpTicketInfo.getRefundQty() + (qty));
                tmpTicketInfo.setMultiply(tmpTicketInfo.getMultiply() + (qty * -1));
                m_aLines.set(i, tmpTicketInfo);
            }
            i++;
        }
    }

    public static void updateRefunds() throws BasicException {
        if (m_aLines != null) {
            for (Object m_aLine : m_aLines) {
                tmpTicketInfo = ((TicketLineInfo) m_aLine);
                dlSales.updateRefundQty(tmpTicketInfo.getRefundQty(), tmpTicketInfo.getTicket(), tmpTicketInfo.getTicketLine());
            }
        }
    }

    private String getPickupString(String pTicket) {
        if (SystemProperty.RECEIPTSIZE >= pTicket.length()) {
            while (pTicket.length() < SystemProperty.RECEIPTSIZE) {
                pTicket = "0" + pTicket;
            }
        }
        return (pTicket);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        m_jbtnAddOne = new javax.swing.JButton();
        m_jbtnAddLine = new javax.swing.JButton();
        m_jbtnAddAll = new javax.swing.JButton();
        m_jbtnExclude = new javax.swing.JButton();

        setFont(KALCFonts.DEFAULTFONT.deriveFont(14f)
        );
        setPreferredSize(new java.awt.Dimension(15, 200));
        setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 0));
        jPanel1.setFont(KALCFonts.DEFAULTFONT.deriveFont(14f)
        );
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setFont(KALCFonts.DEFAULTFONT.deriveFont(14f)
        );
        jPanel2.setLayout(new java.awt.GridLayout(0, 1, 0, 5));

        m_jbtnAddOne.setFont(KALCFonts.DEFAULTBUTTONFONT);
        m_jbtnAddOne.setText(AppLocal.getIntString("button.refundone")); // NOI18N
        m_jbtnAddOne.setFocusPainted(false);
        m_jbtnAddOne.setFocusable(false);
        m_jbtnAddOne.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jbtnAddOne.setMaximumSize(new java.awt.Dimension(125, 50));
        m_jbtnAddOne.setMinimumSize(new java.awt.Dimension(125, 50));
        m_jbtnAddOne.setRequestFocusEnabled(false);
        m_jbtnAddOne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnAddOneActionPerformed(evt);
            }
        });
        jPanel2.add(m_jbtnAddOne);

        m_jbtnAddLine.setFont(KALCFonts.DEFAULTBUTTONFONT);
        m_jbtnAddLine.setText(AppLocal.getIntString("button.refundline")); // NOI18N
        m_jbtnAddLine.setFocusPainted(false);
        m_jbtnAddLine.setFocusable(false);
        m_jbtnAddLine.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jbtnAddLine.setMaximumSize(new java.awt.Dimension(125, 50));
        m_jbtnAddLine.setMinimumSize(new java.awt.Dimension(125, 50));
        m_jbtnAddLine.setRequestFocusEnabled(false);
        m_jbtnAddLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnAddLineActionPerformed(evt);
            }
        });
        jPanel2.add(m_jbtnAddLine);

        m_jbtnAddAll.setFont(KALCFonts.DEFAULTBUTTONFONT);
        m_jbtnAddAll.setText(AppLocal.getIntString("button.refundall")); // NOI18N
        m_jbtnAddAll.setFocusPainted(false);
        m_jbtnAddAll.setFocusable(false);
        m_jbtnAddAll.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jbtnAddAll.setMaximumSize(new java.awt.Dimension(125, 50));
        m_jbtnAddAll.setMinimumSize(new java.awt.Dimension(125, 50));
        m_jbtnAddAll.setRequestFocusEnabled(false);
        m_jbtnAddAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnAddAllActionPerformed(evt);
            }
        });
        jPanel2.add(m_jbtnAddAll);

        m_jbtnExclude.setFont(KALCFonts.DEFAULTBUTTONFONT);
        m_jbtnExclude.setText(AppLocal.getIntString("button.excludesc")); // NOI18N
        m_jbtnExclude.setFocusPainted(false);
        m_jbtnExclude.setFocusable(false);
        m_jbtnExclude.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jbtnExclude.setMaximumSize(new java.awt.Dimension(125, 50));
        m_jbtnExclude.setMinimumSize(new java.awt.Dimension(125, 50));
        m_jbtnExclude.setRequestFocusEnabled(false);
        m_jbtnExclude.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnExcludeActionPerformed(evt);
            }
        });
        jPanel2.add(m_jbtnExclude);

        jPanel1.add(jPanel2, java.awt.BorderLayout.NORTH);

        jPanel3.add(jPanel1, java.awt.BorderLayout.EAST);

        add(jPanel3, java.awt.BorderLayout.WEST);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jbtnAddAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnAddAllActionPerformed
        // Refund all items
        Boolean refunded = true;
        for (Object m_aLine : m_aLines) {
            TicketLineInfo oLine = (TicketLineInfo) m_aLine;

            if (oLine.getMultiply() > 0.0) {
                refunded = false;
                oLine.setRefundQty(oLine.getMultiply());
                oLine.setMultiply(0);
                TicketLineInfo oNewLine = new TicketLineInfo(oLine, true);
                oNewLine.setMultiply(oLine.getRefundQty());
                m_jTicketEdit.addTicketLine(oNewLine);
            }

        }

        if (refunded) {
            JAlertPane.messageBox(JAlertPane.WARNING, AppLocal.getIntString("message.allitemsadded"), 16,
                    new Dimension(100, 50), JAlertPane.OK_OPTION);
        }
    }//GEN-LAST:event_m_jbtnAddAllActionPerformed


    private void m_jbtnAddOneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnAddOneActionPerformed
        // Refund one item from line
        int index = ticketlines.getSelectedIndex();
        if (index >= 0) {
            TicketLineInfo oLine = (TicketLineInfo) m_aLines.get(index);
            if (oLine.isProductCom()) {
                Toolkit.getDefaultToolkit().beep();
                JAlertPane.messageBox(JAlertPane.WARNING, AppLocal.getIntString("message.refundauxiliaryitem") + AppLocal.getIntString("button.refundone"), 16,
                        new Dimension(100, 50), JAlertPane.OK_OPTION);
            } else if (oLine.getMultiply() > 0.0) {

                //check if a service charge has already been added
                if (oLine.getProductID().equalsIgnoreCase("servicecharge") & !serviceCharge) {
                    serviceCharge = true;
                } else if (oLine.getProductID().equalsIgnoreCase("servicecharge") & serviceCharge) {
                    return;
                }

                //check if a service charge has already been added
                if (containsSC & !serviceCharge) {
                    TicketLineInfo serviceLine = (TicketLineInfo) m_aLines.get(scLine);
                    serviceLine.setProperty("orgLine", String.valueOf(scLine));
                    serviceCharge = true;
                    m_aLines.set(scLine, serviceLine);
                    TicketLineInfo oNewLine = new TicketLineInfo(serviceLine, true);
                    oNewLine.setMultiply(1.0);
                    m_jTicketEdit.addTicketLine(oNewLine);
                }

                oLine.setProperty("orgLine", String.valueOf(index));
                if (!oLine.getProductID().equalsIgnoreCase("servicecharge")) {
                    oLine.setRefundQty(oLine.getRefundQty() + 1.0);
                    oLine.setMultiply(oLine.getMultiply() - 1.0);
                }

                m_aLines.set(index, oLine);
                TicketLineInfo oNewLine = new TicketLineInfo(oLine, true);
                oNewLine.setMultiply(1.0);
                m_jTicketEdit.addTicketLine(oNewLine);

                if (index < m_aLines.size() - 1) {
                    oLine = (TicketLineInfo) m_aLines.get(++index);
                    while (index < m_aLines.size() && oLine.isProductCom()) {
                        oLine.setProperty("orgLine", String.valueOf(index));
                        oLine.setRefundQty(oLine.getRefundQty() + 1.0);
                        oLine.setMultiply(oLine.getMultiply() - 1.0);
                        m_aLines.set(index, oLine);
                        oNewLine = new TicketLineInfo(oLine, true);
                        oNewLine.setMultiply(1.0);
                        m_jTicketEdit.addTicketLine(oNewLine);
                        try {
                            oLine = (TicketLineInfo) m_aLines.get(++index);
                        } catch (Exception ex) {
                            break;
                        }
                    }
                }
            } else {
                JAlertPane.messageBox(JAlertPane.WARNING, AppLocal.getIntString("message.allitemsadded"), 16,
                        new Dimension(100, 50), JAlertPane.OK_OPTION);
            }
        }
    }//GEN-LAST:event_m_jbtnAddOneActionPerformed

    private void m_jbtnAddLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnAddLineActionPerformed
        // refund line
        int index = ticketlines.getSelectedIndex();
        if (index >= 0) {
            TicketLineInfo oLine = (TicketLineInfo) m_aLines.get(index);
            if (oLine.isProductCom()) {
                Toolkit.getDefaultToolkit().beep();
                JAlertPane.messageBox(JAlertPane.WARNING, AppLocal.getIntString("message.refundauxiliaryitem") + AppLocal.getIntString("button.refundline"), 16,
                        new Dimension(100, 50), JAlertPane.OK_OPTION);
            } else if (oLine.getMultiply() > 0.0) {
                //check if a service charge has already been added
                if (oLine.getProductID().equalsIgnoreCase("servicecharge") & !serviceCharge) {
                    serviceCharge = true;
                } else if (oLine.getProductID().equalsIgnoreCase("servicecharge") & serviceCharge) {
                    return;
                }

                //check if a service charge  has already been added
                if (containsSC & !serviceCharge) {
                    TicketLineInfo serviceLine = (TicketLineInfo) m_aLines.get(scLine);
                    serviceLine.setProperty("orgLine", String.valueOf(scLine));
                    serviceCharge = true;
                    m_aLines.set(scLine, serviceLine);
                    TicketLineInfo oNewLine = new TicketLineInfo(serviceLine, true);
                    oNewLine.setMultiply(1.0);
                    m_jTicketEdit.addTicketLine(oNewLine);
                }

                oLine.setProperty("orgLine", String.valueOf(index));
                if (!oLine.getProductID().equalsIgnoreCase("servicecharge")) {
                    oLine.setRefundQty(oLine.getMultiply());
                    oLine.setMultiply(0);
                }
                m_aLines.set(index, oLine);
                TicketLineInfo oNewLine = new TicketLineInfo(oLine, true);
                oNewLine.setMultiply(oLine.getRefundQty());
                m_jTicketEdit.addTicketLine(oNewLine);
                oNewLine.setMultiply(oLine.getRefundQty());

                if (index < m_aLines.size() - 1) {
                    oLine = (TicketLineInfo) m_aLines.get(++index);
                    while (index < m_aLines.size() && oLine.isProductCom()) {
                        oLine.setProperty("orgLine", String.valueOf(index));
                        oNewLine = new TicketLineInfo(oLine, true);
                        oNewLine.setMultiply(oLine.getMultiply());
                        oLine.setRefundQty(oLine.getRefundQty() + oLine.getMultiply());
                        oLine.setMultiply(oLine.getMultiply() - oLine.getMultiply());
                        m_aLines.set(index, oLine);
                        m_jTicketEdit.addTicketLine(oNewLine);
                        try {
                            oLine = (TicketLineInfo) m_aLines.get(++index);
                        } catch (Exception ex) {
                            break;
                        }
                    }
                }
            } else {
                JAlertPane.messageBox(JAlertPane.WARNING, AppLocal.getIntString("message.allitemsadded"), 16,
                        new Dimension(100, 50), JAlertPane.OK_OPTION);
            }
        }
    }//GEN-LAST:event_m_jbtnAddLineActionPerformed

    private void m_jbtnExcludeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnExcludeActionPerformed

        m_jTicketEdit.removeSCLine();
    }//GEN-LAST:event_m_jbtnExcludeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JButton m_jbtnAddAll;
    private javax.swing.JButton m_jbtnAddLine;
    private javax.swing.JButton m_jbtnAddOne;
    private javax.swing.JButton m_jbtnExclude;
    // End of variables declaration//GEN-END:variables

}
