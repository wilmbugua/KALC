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


package uk.kalc.pos.payment;

import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import uk.kalc.format.Formats;
import uk.kalc.pos.customers.CustomerInfoExt;
import uk.kalc.pos.forms.AppLocal;
import uk.kalc.pos.forms.KALCFonts;
import uk.kalc.pos.loyalty.LoyaltyCard;
import uk.kalc.pos.util.RoundUtils;

public class JPaymentDebt extends javax.swing.JPanel implements JPaymentInterface {

    private final JPaymentNotifier notifier;
    private CustomerInfoExt customerext;
    private double m_dPaid;
    private double m_dTotal;

    /**
     * Creates new form JPaymentDebt
     *
     * @param notifier
     */
    public JPaymentDebt(JPaymentNotifier notifier) {

        this.notifier = notifier;

        initComponents();
        m_jTendered.addPropertyChangeListener("Edition", new RecalculateState());
        m_jTendered.addEditorKeys(m_jKeys);

    }

    /**
     *
     * @param customerext
     * @param dTotal
     * @param transID
     */
    @Override
    public void activate(CustomerInfoExt customerext, double dTotal, String transID) {

        this.customerext = customerext;
        m_dTotal = dTotal;

        m_jTendered.reset();

        if (customerext == null) {
            m_jName.setText(null);
            m_jNotes.setText(null);
            txtMaxdebt.setText(null);
            txtDiscount.setText(null);
            txtCurdate.setText(null);
            txtCurdebt.setText(null);

            m_jKeys.setEnabled(false);
            m_jTendered.setEnabled(false);

        } else {
            m_jName.setText(customerext.getName());
            m_jNotes.setText(customerext.getNotes());
            txtMaxdebt.setText(Formats.CURRENCY.formatValue(RoundUtils.getValue(customerext.getMaxDebt())));
            txtDiscount.setText(Formats.PERCENT.formatValue(RoundUtils.getValue(customerext.getCustomerDiscount())));
            txtCurdate.setText(Formats.DATE.formatValue(customerext.getCurDate()));
            txtCurdebt.setText(Formats.CURRENCY.formatValue(RoundUtils.getValue(customerext.getCurrentDebt())));

            if (RoundUtils.compare(RoundUtils.getValue(customerext.getCurrentDebt()), RoundUtils.getValue(customerext.getMaxDebt())) >= 0) {
                m_jKeys.setEnabled(false);
                m_jTendered.setEnabled(false);
            } else {
                m_jKeys.setEnabled(true);
                m_jTendered.setEnabled(true);
                m_jTendered.activate();
            }
        }

        printState();

    }

    /**
     *
     * @return
     */
    @Override
    public PaymentInfo executePayment() {
        return new PaymentInfoTicket(m_dPaid, "debt");
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public Component getComponent(LoyaltyCard loyaltyCard) {
        return this;
    }

    private void printState() {

        m_jMoneyEuros.setForeground(Color.BLACK);
        if (customerext == null) {
            m_jMoneyEuros.setText(null);
            jlblMessage.setText(AppLocal.getIntString("message.nocustomernodebt"));
            notifier.setStatus(false, false);
        } else {
            Double value = m_jTendered.getDoubleValue();
            if (value == null || value == 0.0) {
                m_dPaid = m_dTotal;
            } else {
                m_dPaid = value;
            }

            m_jMoneyEuros.setText(Formats.CURRENCY.formatValue(m_dPaid));

            if (RoundUtils.compare(RoundUtils.getValue(customerext.getCurrentDebt()) + m_dPaid, RoundUtils.getValue(customerext.getMaxDebt())) >= 0) {
                // maximum debt exceded
                jlblMessage.setText(AppLocal.getIntString("message.customerdebtexceded"));
                notifier.setStatus(false, false);
            } else {
                jlblMessage.setText(null);
                int iCompare = RoundUtils.compare(m_dPaid, m_dTotal);
                // if iCompare > 0 then the payment is not valid
                notifier.setStatus(m_dPaid > 0.0 && iCompare <= 0, iCompare == 0);
            }
        }
    }

    private class RecalculateState implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            printState();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        m_jMoneyEuros = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        m_jName = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtMaxdebt = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtCurdebt = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtCurdate = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        m_jNotes = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        txtDiscount = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jlblMessage = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        m_jKeys = new uk.kalc.editor.JEditorKeys();
        jPanel3 = new javax.swing.JPanel();
        m_jTendered = new uk.kalc.editor.JEditorCurrencyPositive();

        setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(null);

        jLabel8.setFont(KALCFonts.DEFAULTFONT);
        jLabel8.setText(AppLocal.getIntString("label.debt")); // NOI18N
        jPanel4.add(jLabel8);
        jLabel8.setBounds(20, 20, 100, 25);

        m_jMoneyEuros.setBackground(new java.awt.Color(204, 255, 51));
        m_jMoneyEuros.setFont(KALCFonts.DEFAULTFONT);
        m_jMoneyEuros.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jMoneyEuros.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jMoneyEuros.setOpaque(true);
        m_jMoneyEuros.setPreferredSize(new java.awt.Dimension(200, 30));
        jPanel4.add(m_jMoneyEuros);
        m_jMoneyEuros.setBounds(140, 20, 210, 30);

        jLabel3.setFont(KALCFonts.DEFAULTFONT);
        jLabel3.setText(AppLocal.getIntString("label.name")); // NOI18N
        jPanel4.add(jLabel3);
        jLabel3.setBounds(20, 60, 100, 25);

        m_jName.setEditable(false);
        m_jName.setFont(KALCFonts.DEFAULTFONT);
        jPanel4.add(m_jName);
        m_jName.setBounds(140, 60, 210, 25);

        jLabel12.setFont(KALCFonts.DEFAULTFONT);
        jLabel12.setText(AppLocal.getIntString("label.notes")); // NOI18N
        jPanel4.add(jLabel12);
        jLabel12.setBounds(20, 90, 100, 25);

        jLabel2.setFont(KALCFonts.DEFAULTFONT);
        jLabel2.setText(AppLocal.getIntString("label.maxdebt")); // NOI18N
        jPanel4.add(jLabel2);
        jLabel2.setBounds(20, 140, 120, 25);

        txtMaxdebt.setEditable(false);
        txtMaxdebt.setFont(KALCFonts.DEFAULTFONT);
        txtMaxdebt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel4.add(txtMaxdebt);
        txtMaxdebt.setBounds(140, 140, 210, 25);

        jLabel4.setFont(KALCFonts.DEFAULTFONT);
        jLabel4.setText(AppLocal.getIntString("label.curdebt")); // NOI18N
        jPanel4.add(jLabel4);
        jLabel4.setBounds(20, 200, 100, 25);

        txtCurdebt.setEditable(false);
        txtCurdebt.setFont(KALCFonts.DEFAULTFONT);
        txtCurdebt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel4.add(txtCurdebt);
        txtCurdebt.setBounds(140, 200, 210, 25);

        jLabel6.setFont(KALCFonts.DEFAULTFONT);
        jLabel6.setText(AppLocal.getIntString("label.curdate")); // NOI18N
        jPanel4.add(jLabel6);
        jLabel6.setBounds(20, 230, 100, 25);

        txtCurdate.setEditable(false);
        txtCurdate.setFont(KALCFonts.DEFAULTFONT);
        txtCurdate.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel4.add(txtCurdate);
        txtCurdate.setBounds(140, 230, 110, 25);

        m_jNotes.setEditable(false);
        m_jNotes.setBackground(new java.awt.Color(240, 240, 240));
        m_jNotes.setFont(KALCFonts.DEFAULTFONT);
        m_jNotes.setEnabled(false);
        jScrollPane1.setViewportView(m_jNotes);

        jPanel4.add(jScrollPane1);
        jScrollPane1.setBounds(140, 90, 210, 40);

        jLabel5.setFont(KALCFonts.DEFAULTFONT);
        jLabel5.setText(AppLocal.getIntString("label.discount")); // NOI18N
        jPanel4.add(jLabel5);
        jLabel5.setBounds(20, 170, 100, 25);

        txtDiscount.setEditable(false);
        txtDiscount.setFont(KALCFonts.DEFAULTFONT);
        txtDiscount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel4.add(txtDiscount);
        txtDiscount.setBounds(140, 170, 70, 25);

        jlblMessage.setEditable(false);
        jlblMessage.setFont(KALCFonts.DEFAULTFONT);
        jlblMessage.setForeground(new java.awt.Color(255, 0, 51));
        jlblMessage.setLineWrap(true);
        jlblMessage.setWrapStyleWord(true);
        jlblMessage.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jlblMessage.setFocusable(false);
        jlblMessage.setPreferredSize(new java.awt.Dimension(300, 72));
        jlblMessage.setRequestFocusEnabled(false);
        jPanel6.add(jlblMessage);

        jPanel4.add(jPanel6);
        jPanel6.setBounds(0, 262, 300, 80);

        jPanel5.add(jPanel4, java.awt.BorderLayout.CENTER);

        add(jPanel5, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        m_jKeys.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jKeysActionPerformed(evt);
            }
        });
        jPanel1.add(m_jKeys);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel3.setLayout(new java.awt.BorderLayout());

        m_jTendered.setFont(KALCFonts.DEFAULTFONT);
        jPanel3.add(m_jTendered, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel3);

        jPanel2.add(jPanel1, java.awt.BorderLayout.NORTH);

        add(jPanel2, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jKeysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jKeysActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_m_jKeysActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jlblMessage;
    private uk.kalc.editor.JEditorKeys m_jKeys;
    private javax.swing.JLabel m_jMoneyEuros;
    private javax.swing.JTextField m_jName;
    private javax.swing.JTextArea m_jNotes;
    private uk.kalc.editor.JEditorCurrencyPositive m_jTendered;
    private javax.swing.JTextField txtCurdate;
    private javax.swing.JTextField txtCurdebt;
    private javax.swing.JTextField txtDiscount;
    private javax.swing.JTextField txtMaxdebt;
    // End of variables declaration//GEN-END:variables
}
