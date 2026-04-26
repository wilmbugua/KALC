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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import uk.kalc.globals.SystemProperty;
import uk.kalc.data.gui.MessageInf;
import uk.kalc.format.Formats;
import uk.kalc.globals.IconFactory;
import uk.kalc.pos.customers.CustomerInfoExt;
import uk.kalc.pos.forms.AppLocal;
import uk.kalc.pos.datalogic.DataLogicSystem;
import uk.kalc.pos.forms.KALCFonts;
import uk.kalc.pos.loyalty.LoyaltyCard;
import uk.kalc.pos.scripting.ScriptEngine;
import uk.kalc.pos.scripting.ScriptException;
import uk.kalc.pos.scripting.ScriptFactory;
import uk.kalc.pos.util.RoundUtils;
import uk.kalc.pos.util.ThumbNailBuilder;

public class JPaymentCustom extends javax.swing.JPanel implements JPaymentInterface {

    private final JPaymentNotifier m_notifier;

    private double m_dTotal;
    private double m_dTicket;
    private String m_sCustom;

    /**
     * Creates new form JPaymentCustom
     *
     * @param notifier
     * @param dlSystem
     */
    public JPaymentCustom(JPaymentNotifier notifier, String sCustom, DataLogicSystem dlSystem) {

        m_notifier = notifier;
        m_sCustom = sCustom;

        initComponents();
        m_jTendered.addPropertyChangeListener("Edition", new RecalculateState());
        m_jTendered.addEditorKeys(m_jKeys);

        String code = dlSystem.getResourceAsXML("payment.custom");
        if (code != null) {
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);
                script.put("payment", new ScriptPaymentCash(dlSystem));
                script.eval(code);
            } catch (ScriptException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotexecute"), e);
                msg.show(this);
            }
        }

    }

    /**
     *
     * @return
     */
    @Override
    public PaymentInfo executePayment() {

        return new PaymentInfoTicket(m_dTicket, m_sCustom);
    }

    /**
     *
     * @param customerext
     * @param dTotal
     * @param transID
     */
    @Override
    public void activate(CustomerInfoExt customerext, double dTotal, String transID) {

        m_dTotal = dTotal;

        m_jTendered.reset();
        m_jTendered.activate();

        printState();
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
        Double value = m_jTendered.getDoubleValue();
        if (value == null) {
            m_dTicket = 0.0;
        } else {
            m_dTicket = value;
        }

        m_jMoneyEuros.setText(Formats.CURRENCY.formatValue(Double.valueOf(m_dTicket)));

        int iCompare = RoundUtils.compare(m_dTicket, m_dTotal);

        // it is allowed to pay more
        m_notifier.setStatus(m_dTicket > 0.0, iCompare >= 0);
    }

    private class RecalculateState implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            printState();
        }
    }

    /**
     *
     */
    public class ScriptPaymentCash {

        private final DataLogicSystem dlSystem;
        private final ThumbNailBuilder tnbbutton;

        /**
         *
         * @param dlSystem
         */
        public ScriptPaymentCash(DataLogicSystem dlSystem) {
//added 19.04.13 JDL        
            this.dlSystem = dlSystem;
            tnbbutton = new ThumbNailBuilder(64, 50, IconFactory.getIcon("cash.png"));
        }

        /**
         *
         * @param image
         * @param amount
         */
        public void addButton(String image, double amount) {
            JButton btn = new JButton();
            try {
                if (SystemProperty.TEXTOVERLAY) {
                    btn.setIcon(new ImageIcon(tnbbutton.getThumbNailText(dlSystem.getResourceAsImage(image), "")));
                } else {
                    btn.setIcon(new ImageIcon(tnbbutton.getThumbNailText(dlSystem.getResourceAsImage(image), Formats.CURRENCY.formatValue(amount))));
                }
            } catch (Exception e) {
                btn.setIcon(new ImageIcon(tnbbutton.getThumbNailText(dlSystem.getResourceAsImage(image), Formats.CURRENCY.formatValue(amount))));
            }

            btn.setFocusPainted(false);
            btn.setFocusable(false);
            btn.setRequestFocusEnabled(false);
            btn.setHorizontalTextPosition(SwingConstants.CENTER);
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);
            btn.setMargin(new Insets(2, 2, 2, 2));
            btn.addActionListener(new AddAmount(amount));
            jPanel6.add(btn);
        }
    }

    private class AddAmount implements ActionListener {

        private final double amount;

        public AddAmount(double amount) {
            this.amount = amount;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Double tendered = m_jTendered.getDoubleValue();

            if (tendered == null) {
                m_jTendered.setDoubleValue(amount);
            } else {
                m_jTendered.setDoubleValue(tendered + amount);

            }

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
        jPanel6 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        m_jKeys = new uk.kalc.editor.JEditorKeys();
        jPanel3 = new javax.swing.JPanel();
        m_jTendered = new uk.kalc.editor.JEditorCurrencyPositive();

        setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel4.setFont(KALCFonts.DEFAULTFONT.deriveFont(14f)
        );
        jPanel4.setPreferredSize(new java.awt.Dimension(0, 70));
        jPanel4.setLayout(null);

        jLabel8.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(22f)
        );
        jLabel8.setText(AppLocal.getIntString("label.inputCash")); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(100, 30));
        jPanel4.add(jLabel8);
        jLabel8.setBounds(10, 4, 100, 30);

        m_jMoneyEuros.setBackground(new java.awt.Color(204, 255, 51));
        m_jMoneyEuros.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(22f)
        );
        m_jMoneyEuros.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jMoneyEuros.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jMoneyEuros.setOpaque(true);
        m_jMoneyEuros.setPreferredSize(new java.awt.Dimension(180, 30));
        jPanel4.add(m_jMoneyEuros);
        m_jMoneyEuros.setBounds(120, 4, 200, 30);

        jPanel5.add(jPanel4, java.awt.BorderLayout.NORTH);

        jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        jPanel5.add(jPanel6, java.awt.BorderLayout.CENTER);

        add(jPanel5, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));
        jPanel1.add(m_jKeys);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel3.setLayout(new java.awt.BorderLayout());

        m_jTendered.setFont(KALCFonts.DEFAULTFONTBOLD);
        jPanel3.add(m_jTendered, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel3);

        jPanel2.add(jPanel1, java.awt.BorderLayout.NORTH);

        add(jPanel2, java.awt.BorderLayout.LINE_END);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private uk.kalc.editor.JEditorKeys m_jKeys;
    private javax.swing.JLabel m_jMoneyEuros;
    private uk.kalc.editor.JEditorCurrencyPositive m_jTendered;
    // End of variables declaration//GEN-END:variables

}
