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


package ke.kalc.pos.payment;

import java.awt.Color;
import ke.kalc.pos.giftcards.GiftCardInfo;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import ke.kalc.commons.dialogs.JAlertPane;
import ke.kalc.format.Formats;
import ke.kalc.pos.barcodes.Barcode;
import ke.kalc.pos.customers.CustomerInfoExt;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.KALCFonts;
import ke.kalc.pos.giftcards.GiftCardLogic;
import ke.kalc.pos.loyalty.LoyaltyCard;
import ke.kalc.pos.util.RoundUtils;

public class JPaymentGiftCard extends javax.swing.JPanel implements JPaymentInterface {

    private final JPaymentNotifier m_notifier;
    private double m_dPaid;
    private double m_dTotal;
    private int cardNumberLength;
    private HashMap<String, GiftCardInfo> cardMap;
    private GiftCardInfo temp = new GiftCardInfo();
    private Barcode bar;
    private GiftCardLogic giftCard = new GiftCardLogic();

    public JPaymentGiftCard(JPaymentNotifier notifier) {
        m_notifier = notifier;
        initComponents();
        jBalanceBtn.setText(AppLocal.getIntString("label.cardBalance"));
        m_jTendered.addPropertyChangeListener("Edition", new RecalculateState());
        m_jTendered.addEditorKeys(m_jKeys);
        m_jTendered.setVisible(false);
        m_jCardNumber.setVisible(false);
        m_jCardNumber.addPropertyChangeListener("Edition", new ShowCardNumber());
        m_jCardNumber.addEditorKeys(m_jKeys);
    }

    public void resetCardMap() {
        cardMap = new HashMap();
    }

    @Override
    public void activate(CustomerInfoExt customerext, double dTotal, String transID) {
        m_dTotal = dTotal;
        m_jTendered.reset();
        m_jTendered.activate();
        m_jTendered.setVisible(false);
        m_jCardNumber.reset();
        m_jCardNumber.activate();
        jCardNumber.setText("");
        cardValue.setText(Formats.CURRENCY.formatValue(0.00));
        jBalanceBtn.setVisible(true);
        printState();
    }

    @Override
    public PaymentInfo executePayment() {
        if (bar.isValid()) {
            temp = cardMap.get(jCardNumber.getText());
            if (temp.checkValue(m_jTendered.getText())) {
                JAlertPane.messageBox(JAlertPane.WARNING, AppLocal.getIntString("message.insufficientfunds"), 16, new Dimension(125, 50), JAlertPane.OK_OPTION);
                m_jTendered.reset();
                m_jTendered.activate();
                printState();
                return null;
            }
            temp.updateCardTransaction(m_jTendered.getText());
            if (m_dPaid - m_dTotal >= 0.0) {
                return new PaymentInfoGiftCard(m_dTotal, m_dTotal, jCardNumber.getText(), giftCard.getCardBalance(bar.getBarCode()));
            } else {
                return new PaymentInfoGiftCard(m_dPaid, m_dPaid, jCardNumber.getText(), giftCard.getCardBalance(bar.getBarCode()));
            }
        } else {
            JAlertPane.messageBox(JAlertPane.WARNING, bar.getBarCode() + AppLocal.getIntString("message.invalidgiftcard"), 16, new Dimension(125, 50), JAlertPane.OK_OPTION);
            return null;
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

    
//    public String getCardName(){
//        
//    }
    
    private void printCardNumber() {
        if (m_jCardNumber.getText() != null) {
            m_notifier.setDefaultBtn(false);
            //Check if return has been received into the buffer and cardnumber length is valid
            if (m_jCardNumber.getText().length() > 0) {
                if (Character.getNumericValue(m_jCardNumber.getText().charAt(m_jCardNumber.getText().length() - 1)) == -1) {
                    bar = new Barcode(m_jCardNumber.getText());
                    if (bar.isEOF()) {
                        setCardDetails();
                        return;
                    }
                }
            }
            jCardNumber.setText(m_jCardNumber.getText());
        } else {
            m_jCardNumber.reset();
            jCardNumber.setText("");
            m_jCardNumber.activate();
            printState();
        }
    }

    private void setCardDetails() {
        if (bar.isValid() && bar.getBarcodeType() == Barcode.GIFTCARD) {
            m_notifier.setDefaultBtn(true);
            m_jTendered.activate();
            jCardNumber.setText(bar.getBarCode());

            if (!cardMap.containsKey(bar.getBarCode())) {
                cardMap.put(bar.getBarCode(), new GiftCardInfo(Double.toString(giftCard.getCardBalance(jCardNumber.getText())),
                        Double.toString(giftCard.getCardBalance(jCardNumber.getText())), "0.00"));
            }

            cardValue.setText(Formats.CURRENCY.formatValue(Double.parseDouble(cardMap.get(bar.getBarCode()).getRemainingValue())));

            if (m_dTotal >= giftCard.getCardBalance(jCardNumber.getText())) {
                m_jMoneyEuros.setText(Formats.CURRENCY.formatValue(giftCard.getCardBalance(jCardNumber.getText())));
                m_jTendered.setDoubleValue(giftCard.getCardBalance(jCardNumber.getText()));
            } else {
                m_jMoneyEuros.setText(Formats.CURRENCY.formatValue(m_dTotal));
                m_jTendered.setDoubleValue(m_dTotal);
            }
            //m_jTendered.setVisible(true);
            jBalanceBtn.setVisible(false);
        } else {
            m_jCardNumber.reset();
            jCardNumber.setText("");
            JAlertPane.messageBox(JAlertPane.WARNING, bar.getBarCode() + AppLocal.getIntString("message.invalidgiftcard"), 16, new Dimension(125, 50), JAlertPane.OK_OPTION);
            m_jCardNumber.activate();
        }
        printState();
    }

    private void printState() {
        m_jMoneyEuros.setForeground(Color.BLACK);
        Double value = m_jTendered.getDoubleValue();
        if (value == null || value == 0.0) {
            m_dPaid = 0.00;
        } else {
            m_dPaid = value;
        }
        int iCompare = RoundUtils.compare(m_dPaid, m_dTotal);
        m_jMoneyEuros.setText(Formats.CURRENCY.formatValue(m_dPaid));
        m_notifier.setStatus(m_dPaid > 0.0, iCompare >= 0);
    }

    private class RecalculateState implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            printState();
        }
    }

    private class ShowCardNumber implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            printCardNumber();
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cardValue = new javax.swing.JLabel();
        jCardNumber = new javax.swing.JLabel();
        jBalanceBtn = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        m_jKeys = new ke.kalc.editor.JEditorKeys();
        jPanel3 = new javax.swing.JPanel();
        m_jTendered = new ke.kalc.editor.JEditorCurrencyPositive();
        jPanel7 = new javax.swing.JPanel();
        m_jCardNumber = new ke.kalc.editor.JEditorGiftCard();

        setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel4.setFont(KALCFonts.DEFAULTFONT.deriveFont(14f)
        );
        jPanel4.setPreferredSize(new java.awt.Dimension(0, 40));
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

        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(22f)
        );
        jLabel1.setText("Card No.");
        jLabel1.setPreferredSize(new java.awt.Dimension(100, 30));
        jPanel6.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, -1));

        jLabel2.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(22f)
        );
        jLabel2.setText("Value");
        jLabel2.setPreferredSize(new java.awt.Dimension(100, 30));
        jPanel6.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 100, -1));

        cardValue.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(22f)
        );
        cardValue.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(java.awt.SystemColor.controlDkShadow), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        cardValue.setPreferredSize(new java.awt.Dimension(60, 30));
        jPanel6.add(cardValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 50, 200, -1));

        jCardNumber.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(22f)
        );
        jCardNumber.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(java.awt.SystemColor.controlDkShadow), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        jCardNumber.setPreferredSize(new java.awt.Dimension(180, 30));
        jPanel6.add(jCardNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 10, 200, 30));

        jBalanceBtn.setFont(KALCFonts.DEFAULTBUTTONFONT);
        jBalanceBtn.setText("jButton1");
        jBalanceBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBalanceBtnActionPerformed(evt);
            }
        });
        jPanel6.add(jBalanceBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 90, 180, 30));

        jPanel5.add(jPanel6, java.awt.BorderLayout.CENTER);

        add(jPanel5, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));
        jPanel1.add(m_jKeys);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel3.setLayout(new java.awt.BorderLayout());

        m_jTendered.setFont(KALCFonts.DEFAULTFONT.deriveFont(18f)
        );
        jPanel3.add(m_jTendered, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel3);
        jPanel1.add(jPanel7);

        jPanel2.add(jPanel1, java.awt.BorderLayout.NORTH);

        m_jCardNumber.setFont(KALCFonts.DEFAULTFONT.deriveFont(12f));
        jPanel2.add(m_jCardNumber, java.awt.BorderLayout.PAGE_END);

        add(jPanel2, java.awt.BorderLayout.LINE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void jBalanceBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBalanceBtnActionPerformed
        //get card balance from table
        bar = new Barcode(m_jCardNumber.getText());
        setCardDetails();
        cardValue.setText(Formats.CURRENCY.formatValue(giftCard.getCardBalance(bar.getBarCode())));
        m_notifier.setDefaultBtn(true);
        m_jTendered.activate();
    }//GEN-LAST:event_jBalanceBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel cardValue;
    private javax.swing.JButton jBalanceBtn;
    private javax.swing.JLabel jCardNumber;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private ke.kalc.editor.JEditorGiftCard m_jCardNumber;
    private ke.kalc.editor.JEditorKeys m_jKeys;
    private javax.swing.JLabel m_jMoneyEuros;
    private ke.kalc.editor.JEditorCurrencyPositive m_jTendered;
    // End of variables declaration//GEN-END:variables

}
