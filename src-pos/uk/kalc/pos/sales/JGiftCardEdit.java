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


package uk.kalc.pos.sales;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFrame;
import uk.kalc.basic.BasicException;
import uk.kalc.format.Formats;
import uk.kalc.commons.dialogs.JAlertPane;
import uk.kalc.pos.forms.AppLocal;
import uk.kalc.pos.forms.AppView;
import uk.kalc.pos.datalogic.DataLogicSales;

import uk.kalc.pos.giftcards.GiftCardLogic;
import uk.kalc.pos.ticket.ProductInfoExt;
import uk.kalc.pos.ticket.TicketLineInfo;
import uk.kalc.globals.IconFactory;
import uk.kalc.pos.forms.KALCFonts;

public class JGiftCardEdit extends javax.swing.JDialog {
    
    private TicketLineInfo returnLine;
    private ProductInfoExt product = new ProductInfoExt();
    private GiftCardLogic giftCard = new GiftCardLogic();
    private String cardNumber;
  //  private SentenceList senttax;
    
    private JGiftCardEdit(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }
    
    private JGiftCardEdit(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
    }
    
    private TicketLineInfo init(Window window, AppView app, String cardNumber, DataLogicSales dlSales, Boolean newCard) throws BasicException {
        initComponents();
        this.cardNumber = cardNumber;
        jLblCardNumber.setText(AppLocal.getIntString("label.cardnumber"));
        jLblExisting.setText(AppLocal.getIntString("label.currentBalance"));
        jLblAmount.setText(AppLocal.getIntString("label.paymenttotal"));
        
        m_jAmount.addPropertyChangeListener("Edition", new JGiftCardEdit.CardValue());
        m_jAmount.addEditorKeys(m_jKeys);
        m_jAmount.setVisible(false);
        m_jAmount.activate();
        
        m_jCardNumber.setText(cardNumber);
        m_jAmount.setDoubleValue(0.00);
        
        if (newCard) {
            this.setTitle(AppLocal.getIntString("message.topupGiftCard"));
            product = dlSales.getProductInfoNoSC("giftcard-topup");
            jLblExistingAmount.setText(Formats.CURRENCY.formatValue(giftCard.getCardBalance(cardNumber)));
        } else {
            this.setTitle(AppLocal.getIntString("message.newGiftCardTitle"));
            product = dlSales.getProductInfoNoSC("giftcard-sale");
            jLblExisting.setVisible(false);
            jLblExistingAmount.setVisible(false);
            m_jMoneyEuros.setLocation(160, 50);
            jLblAmount.setLocation(10, 50);
        }
        
        getRootPane().setDefaultButton(m_jButtonOK);
        returnLine = null;
        setLocationRelativeTo(window);
        setVisible(true);
               
        //   returnLine = new TicketLineInfo(product.getID(), product.getName(), product.getTaxCategoryID(), 1.00, Double.valueOf(m_jAmount.getText()), null);
        return returnLine;
    }
    
    private class CardValue implements PropertyChangeListener {
        
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            Double value = m_jAmount.getDoubleValue();
            m_jMoneyEuros.setText(Formats.CURRENCY.formatValue(value));
        }
    }
    
    private static Window getWindow(Component parent) {
        if (parent == null) {
            return new JFrame();
        } else if (parent instanceof Frame || parent instanceof Dialog) {
            return (Window) parent;
        } else {
            return getWindow(parent.getParent());
        }
    }

    /**
     *
     * @param parent
     * @param app
     * @param oLine
     * @return
     * @throws BasicException
     */
    public static TicketLineInfo showMessage(Component parent, AppView app, String cardNumber, DataLogicSales dlSales, Boolean newcard) throws BasicException {
        
        Window window = getWindow(parent);
        JGiftCardEdit myMsg;
        if (window instanceof Frame) {
            myMsg = new JGiftCardEdit((Frame) window, true);
        } else {
            myMsg = new JGiftCardEdit((Dialog) window, true);
        }
        return myMsg.init(window, app, cardNumber, dlSales, newcard);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLblAmount = new javax.swing.JLabel();
        jLblCardNumber = new javax.swing.JLabel();
        m_jMoneyEuros = new javax.swing.JLabel();
        m_jCardNumber = new javax.swing.JLabel();
        jLblExisting = new javax.swing.JLabel();
        jLblExistingAmount = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        m_jButtonCancel = new javax.swing.JButton();
        m_jButtonOK = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        m_jKeys = new uk.kalc.editor.JEditorKeys();
        m_jAmount = new uk.kalc.editor.JEditorCurrency();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(AppLocal.getIntString("label.editline")); // NOI18N

        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(null);

        jLblAmount.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(20f)
        );
        jLblAmount.setText(AppLocal.getIntString("label.units")); // NOI18N
        jPanel2.add(jLblAmount);
        jLblAmount.setBounds(10, 80, 140, 25);

        jLblCardNumber.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(20f));
        jLblCardNumber.setText(AppLocal.getIntString("label.item")); // NOI18N
        jLblCardNumber.setPreferredSize(new java.awt.Dimension(92, 25));
        jPanel2.add(jLblCardNumber);
        jLblCardNumber.setBounds(10, 20, 140, 25);

        m_jMoneyEuros.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(20f));
        m_jMoneyEuros.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(java.awt.SystemColor.controlDkShadow), javax.swing.BorderFactory.createEmptyBorder(1, 4, 4, 1)));
        jPanel2.add(m_jMoneyEuros);
        m_jMoneyEuros.setBounds(160, 80, 130, 25);

        m_jCardNumber.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(20f));
        m_jCardNumber.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(java.awt.SystemColor.controlDkShadow), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jCardNumber.setPreferredSize(new java.awt.Dimension(60, 25));
        jPanel2.add(m_jCardNumber);
        m_jCardNumber.setBounds(160, 20, 250, 25);

        jLblExisting.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(20f)
        );
        jLblExisting.setText("Existing");
        jLblExisting.setPreferredSize(new java.awt.Dimension(50, 25));
        jPanel2.add(jLblExisting);
        jLblExisting.setBounds(10, 50, 140, 25);

        jLblExistingAmount.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(20f));
        jLblExistingAmount.setBorder(m_jCardNumber.getBorder());
        jPanel2.add(jLblExistingAmount);
        jLblExistingAmount.setBounds(160, 50, 130, 25);

        jPanel5.add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        m_jButtonCancel.setFont(KALCFonts.DEFAULTBUTTONFONT);
        m_jButtonCancel.setIcon(IconFactory.getIcon("cancel.png"));
        m_jButtonCancel.setText(AppLocal.getIntString("button.cancel")); // NOI18N
        m_jButtonCancel.setFocusPainted(false);
        m_jButtonCancel.setFocusable(false);
        m_jButtonCancel.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jButtonCancel.setRequestFocusEnabled(false);
        m_jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jButtonCancelActionPerformed(evt);
            }
        });
        jPanel1.add(m_jButtonCancel);

        m_jButtonOK.setFont(KALCFonts.DEFAULTBUTTONFONT);
        m_jButtonOK.setIcon(IconFactory.getIcon("ok.png"));
        m_jButtonOK.setText(AppLocal.getIntString("button.ok")); // NOI18N
        m_jButtonOK.setFocusPainted(false);
        m_jButtonOK.setFocusable(false);
        m_jButtonOK.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jButtonOK.setRequestFocusEnabled(false);
        m_jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jButtonOKActionPerformed(evt);
            }
        });
        jPanel1.add(m_jButtonOK);

        jPanel5.add(jPanel1, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel5, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.Y_AXIS));

        m_jKeys.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jKeysActionPerformed(evt);
            }
        });
        jPanel4.add(m_jKeys);

        jPanel3.add(jPanel4, java.awt.BorderLayout.NORTH);

        m_jAmount.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel3.add(m_jAmount, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel3, java.awt.BorderLayout.EAST);

        setSize(new java.awt.Dimension(644, 362));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jButtonCancelActionPerformed
        returnLine = null;
        dispose();


    }//GEN-LAST:event_m_jButtonCancelActionPerformed

    private void m_jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jButtonOKActionPerformed
        if (Double.valueOf(m_jAmount.getText()) != 0.00) {
            if (Double.valueOf(m_jAmount.getText()) >= 500.00) {

                if (JAlertPane.messageBox(JAlertPane.CONFIRMATION, AppLocal.getIntString("alert.cardValueMessage"), 16,
                        new Dimension(125, 50), JAlertPane.YES_NO_OPTION) == 6) {
                    returnLine = null;
                    dispose();
                    return;
                }
            }

            returnLine = new TicketLineInfo(product, 1.00, Double.valueOf(m_jAmount.getText()), cardNumber);
          //  returnLine = new TicketLineInfo(product.getID(), product.getName(), product.getTaxCategoryID(), 1.00, Double.valueOf(m_jAmount.getText()), null, product.isSystemObject(), cardNumber);
    //        returnLine.setSystemObject(product.isSystemObject());
        } else {
            returnLine = null;
        }
        dispose();
    }//GEN-LAST:event_m_jButtonOKActionPerformed

    private void m_jKeysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jKeysActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_m_jKeysActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLblAmount;
    private javax.swing.JLabel jLblCardNumber;
    private javax.swing.JLabel jLblExisting;
    private javax.swing.JLabel jLblExistingAmount;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private uk.kalc.editor.JEditorCurrency m_jAmount;
    private javax.swing.JButton m_jButtonCancel;
    private javax.swing.JButton m_jButtonOK;
    private javax.swing.JLabel m_jCardNumber;
    private uk.kalc.editor.JEditorKeys m_jKeys;
    private javax.swing.JLabel m_jMoneyEuros;
    // End of variables declaration//GEN-END:variables

}
