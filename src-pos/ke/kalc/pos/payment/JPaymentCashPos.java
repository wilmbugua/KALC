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


package ke.kalc.pos.payment;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import ke.kalc.globals.SystemProperty;
import ke.kalc.data.gui.MessageInf;
import ke.kalc.format.Formats;
import ke.kalc.pos.customers.CustomerInfoExt;
import ke.kalc.commons.dialogs.JAlertPane;
import ke.kalc.globals.IconFactory;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.datalogic.DataLogicSystem;
import ke.kalc.pos.loyalty.LoyaltyCard;
import ke.kalc.pos.scripting.ScriptEngine;
import ke.kalc.pos.scripting.ScriptException;
import ke.kalc.pos.scripting.ScriptFactory;
import ke.kalc.pos.util.RoundUtils;
import ke.kalc.pos.util.ThumbNailBuilder;
import ke.kalc.pos.forms.KALCFonts;

public class JPaymentCashPos extends javax.swing.JPanel implements JPaymentInterface {

    private final JPaymentNotifier m_notifier;

    private double m_dPaid;
    private double m_dTotal;

    /**
     * Creates new form JPaymentCash
     *
     * @param notifier
     * @param dlSystem
     */
    public JPaymentCashPos(JPaymentNotifier notifier, DataLogicSystem dlSystem) {

        m_notifier = notifier;
        initComponents();

        m_jTendered.addPropertyChangeListener("Edition", new RecalculateState());
        m_jTendered.addEditorKeys(m_jKeys);

        m_jKeys.dotIs00(SystemProperty.PRICEWITH00);

        String code = dlSystem.getResourceAsXML("payment.cash");
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

    /**
     *
     * @return
     */
    @Override
    public PaymentInfo executePayment() {
        if (m_dPaid - m_dTotal >= SystemProperty.OVERSCANAMOUNT) {
            if (JAlertPane.messageBox(JAlertPane.CONFIRMATION, AppLocal.getIntString("alert.changemessage"), 16,
                    new Dimension(125, 50), JAlertPane.YES_NO_OPTION) == 6) {
                m_jTendered.setDoubleValue(0.00);
                printState();
                return null;
            }
        }
        if (m_dPaid - m_dTotal >= 0.0) {
            return new PaymentInfoCash_original(m_dTotal, m_dPaid);
        } else {
            return new PaymentInfoCash_original(m_dPaid, m_dPaid);
        }
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

        Double value = m_jTendered.getDoubleValue();
        if (value == null || value == 0.0) {
            m_dPaid = m_dTotal;
        } else {
            m_dPaid = value;

        }

        int iCompare = RoundUtils.compare(m_dPaid, m_dTotal);

        m_jMoneyEuros.setText(Formats.CURRENCY.formatValue(m_dPaid));
        m_jChangeEuros.setText(iCompare > 0
                ? Formats.CURRENCY.formatValue(m_dPaid - m_dTotal)
                : null);

        m_notifier.setStatus(m_dPaid > 0.0, iCompare >= 0);
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
                    if (SystemProperty.LAF.equals("com.jtattoo.plaf.hifi.HiFiLookAndFeel")) {
                        btn.setIcon(new ImageIcon(tnbbutton.getThumbNailText(dlSystem.getResourceAsImage(image), Formats.CURRENCY.formatValue(amount), Color.WHITE)));
                    } else {
                        // btn.setIcon(new ImageIcon(tnbbutton.getThumbNailText("testcoin.png",dlSystem.getResourceAsImage(image), Formats.CURRENCY.formatValue(amount))));
                        btn.setIcon(new ImageIcon(tnbbutton.getThumbNailText(dlSystem.getResourceAsImage(image), Formats.CURRENCY.formatValue(amount))));
                    }
                }
            } catch (Exception e) {
                btn.setIcon(new ImageIcon(tnbbutton.getThumbNailText(dlSystem.getResourceAsImage(image), Formats.CURRENCY.formatValue(amount))));
            }

            btn.setPreferredSize(new Dimension(90, 80));
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

        // This adds the button with its amount assigned to it
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
        m_jChangeEuros = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        m_jMoneyEuros = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        m_jKeys = new ke.kalc.editor.JEditorKeys();
        jPanel3 = new javax.swing.JPanel();
        m_jTendered = new ke.kalc.editor.JEditorCurrencyPositive();

        setMinimumSize(new java.awt.Dimension(190, 145));
        setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel4.setFont(KALCFonts.DEFAULTFONT.deriveFont(14f));
        jPanel4.setPreferredSize(new java.awt.Dimension(0, 70));
        jPanel4.setLayout(null);

        m_jChangeEuros.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(22f)
        );
        m_jChangeEuros.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jChangeEuros.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jChangeEuros.setOpaque(true);
        m_jChangeEuros.setPreferredSize(new java.awt.Dimension(180, 30));
        jPanel4.add(m_jChangeEuros);
        m_jChangeEuros.setBounds(120, 36, 200, 30);

        jLabel6.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(22f)
        );
        jLabel6.setText(AppLocal.getIntString("label.changeCash")); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(100, 30));
        jPanel4.add(jLabel6);
        jLabel6.setBounds(10, 36, 100, 30);

        jLabel8.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(22f));
        jLabel8.setText(AppLocal.getIntString("label.inputCash")); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(100, 30));
        jPanel4.add(jLabel8);
        jLabel8.setBounds(10, 4, 100, 30);

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

        jPanel2.setPreferredSize(new java.awt.Dimension(210, 295));
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
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JLabel m_jChangeEuros;
    private ke.kalc.editor.JEditorKeys m_jKeys;
    private javax.swing.JLabel m_jMoneyEuros;
    private ke.kalc.editor.JEditorCurrencyPositive m_jTendered;
    // End of variables declaration//GEN-END:variables

}
