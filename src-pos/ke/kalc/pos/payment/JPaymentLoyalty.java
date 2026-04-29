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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;
import ke.kalc.globals.SystemProperty;
import ke.kalc.format.Formats;
import ke.kalc.pos.customers.CustomerInfoExt;
import ke.kalc.commons.dialogs.JAlertPane;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.KALCFonts;
import ke.kalc.pos.forms.LocalResource;
import ke.kalc.pos.loyalty.LoyaltyCard;
import ke.kalc.pos.loyalty.LoyaltyExtendedButton;
import ke.kalc.pos.util.RoundUtils;

public class JPaymentLoyalty extends javax.swing.JPanel implements JPaymentInterface {

    private final JPaymentNotifier m_notifier;

    private double m_dPaid;
    private double m_dTotal;

    private LoyaltyCard loyaltyCard = null;
    private final JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
    private final JLabel cardNumberLbl = new JLabel();
    private final JLabel cardNumber = new JLabel();
    private final JLabel cardPointsLbl = new JLabel();
    private final JLabel cardPoints = new JLabel();
    private final JLabel lastActivityDateLbl = new JLabel();
    private final JLabel lastActivityDate = new JLabel();
    private final JLabel lastActivityActionLbl = new JLabel();
    private final JLabel lastActivityAction = new JLabel();
    private final JLabel qty = new JLabel();
    private final JLabel loyaltyOptions = new JLabel("Loyalty Offers");
    private final JLabel loyaltyPointOffer = new JLabel();
    private int openingCardPoints = 0;
    private JButton btn;
    private Boolean resetPoints = true;
    private final JPanel jLoyaltyPane;
    private LoyaltyExtendedButton actionButton;

    public JPaymentLoyalty(JPaymentNotifier notifier) { //, LoyaltyCard loyaltyCard) {
        m_notifier = notifier;

        initComponents();
        m_jTendered.setVisible(false);
        jLoyaltyPane = new JPanel(new MigLayout("insets 10 10 5 2", "[:20:][:120:][:5:][:350:]", "[]2[]2[]2[]2[]15[]"));

        if (SystemProperty.LOYALTYTYPE.equals("earnx")) {
            buildLoyaltyPanel();
        }
        jPanel5.add(jLoyaltyPane);
    }

    private void buildLoyaltyPanel() {
        cardNumberLbl.setText("Card Number  ");
        cardPointsLbl.setText("Current Points ");
        lastActivityDateLbl.setText("Last Activity Date ");
        lastActivityActionLbl.setText("Last Activity");

        jLoyaltyPane.add(new JSeparator(JSeparator.HORIZONTAL), "span, center, gapy 1, growx, wrap");
        jLoyaltyPane.add(cardNumberLbl, "left, span 2");
        jLoyaltyPane.add(new JLabel(":"), "left");
        jLoyaltyPane.add(cardNumber, "left, width 200:200:, wrap");
        jLoyaltyPane.add(cardPointsLbl, "left, span 2");
        jLoyaltyPane.add(new JLabel(":"), "left");
        jLoyaltyPane.add(cardPoints, "left, width 200:200:, wrap");
        jLoyaltyPane.add(lastActivityDateLbl, "left, span 2");
        jLoyaltyPane.add(new JLabel(":"), "left");
        jLoyaltyPane.add(lastActivityDate, "left, width 200:200:, wrap");
        jLoyaltyPane.add(lastActivityActionLbl, "left, span 2");
        jLoyaltyPane.add(new JLabel(":"), "left");
        jLoyaltyPane.add(lastActivityAction, "left, pushx, wrap");

        loyaltyOptions.setOpaque(true);
        Color backGround = loyaltyOptions.getBackground();
        loyaltyOptions.setBackground(loyaltyOptions.getForeground());
        loyaltyOptions.setForeground(backGround);
        jLoyaltyPane.add(loyaltyOptions, "span, left, growx, pushx, wrap");

        populateLoyaltyDetails();

    }

    private void populateLoyaltyDetails() {
        actionButton = new LoyaltyExtendedButton("Redeem Voucher", openingCardPoints);
        jLoyaltyPane.add(qty);

        loyaltyPointOffer.setText(LocalResource.getString("message.loyaltyRewards", SystemProperty.CURRENCYSYMBOL, String.format("%.2f", SystemProperty.REDEEMVALUE), SystemProperty.VOUCHERPOINTS));
        jLoyaltyPane.add(loyaltyPointOffer, "aligny center, span 2");
        jLoyaltyPane.add(actionButton, "right, wrap");

        actionButton.addActionListener((ActionEvent e) -> {
            LoyaltyExtendedButton extBtn = (LoyaltyExtendedButton) e.getSource();
            if (resetPoints) {
                extBtn.setCardBalance(loyaltyCard.getCardBalance());
                resetPoints = false;
            }
            if (extBtn.getCardBalance() < SystemProperty.VOUCHERPOINTS) {
                JAlertPane.showAlertDialog(JAlertPane.INFORMATION,
                        "Loyalty Points",
                        null,
                        "There are not enough points remaining on the card for this.",
                        JAlertPane.OK_OPTION);
            } else {
                int iCompare = RoundUtils.compare(m_dPaid + SystemProperty.REDEEMVALUE, m_dTotal);
                if (RoundUtils.compare(m_dPaid + SystemProperty.REDEEMVALUE, m_dTotal) > 0) {
                    JAlertPane.showAlertDialog(JAlertPane.INFORMATION,
                            "Loyalty Points",
                            "Cannot continue with redemption.",
                            "Remaing amount owed, is less than voucher amount.",
                            JAlertPane.OK_OPTION);
                    return;
                }
                m_jTendered.setDoubleValue(SystemProperty.REDEEMVALUE);
                extBtn.setCardBalance(extBtn.getCardBalance() - SystemProperty.VOUCHERPOINTS);
                qty.setText(Integer.toString(extBtn.getCardBalance() / SystemProperty.VOUCHERPOINTS) + " x ");
                cardPoints.setText(Integer.toString(extBtn.getCardBalance()));
                printState();
            }
        });
    }

    @Override
    public void activate(CustomerInfoExt customerext, double dTotal, String transID) {
        m_dTotal = dTotal;
        m_dPaid = 0.00;
        m_jTendered.reset();
        m_jTendered.setDoubleValue(0.00);
        printState();
    }

    @Override
    public PaymentInfo executePayment() {
        if (m_dPaid - m_dTotal >= 0.0) {
            return new PaymentInfoLoyalty(m_dTotal, m_dPaid);
        } else {
            return new PaymentInfoLoyalty(m_dPaid, m_dPaid);
        }
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public Component getComponent(LoyaltyCard loyaltyCard) {
        this.loyaltyCard = loyaltyCard;
        if (loyaltyCard != null) {
            openingCardPoints = loyaltyCard.getCardBalance();
            cardNumber.setText(loyaltyCard.getCardNumber());
            cardPoints.setText(Integer.toString(openingCardPoints));
            lastActivityDate.setText(loyaltyCard.getLastActivity("date"));
            lastActivityAction.setText(loyaltyCard.getLastActivity("activity"));
            cardPoints.setText(Integer.toString(loyaltyCard.getCardBalance()));
            qty.setText(Integer.toString(loyaltyCard.getCardBalance() / SystemProperty.VOUCHERPOINTS) + " x ");
            actionButton.setVisible(true);
            loyaltyPointOffer.setVisible(true);
            qty.setVisible(true);
            resetPoints = true;
        } else {
            openingCardPoints = 0;
            cardNumber.setText("");
            cardPoints.setText("");
            lastActivityDate.setText("");
            lastActivityAction.setText("");
            resetPoints = true;
            actionButton.setVisible(false);
            loyaltyPointOffer.setVisible(false);
            qty.setVisible(false);
        }
        return this;
    }

    private void printState() {
        m_jMoneyEuros.setForeground(Color.BLACK);
        Double value = m_jTendered.getDoubleValue();
        m_dPaid += value;
        int iCompare = RoundUtils.compare(m_dPaid, m_dTotal);
        m_jMoneyEuros.setText(Formats.CURRENCY.formatValue(m_dPaid));
        m_notifier.setStatus(m_dPaid > 0.0, iCompare >= 0);
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
        m_jTendered = new ke.kalc.editor.JEditorCurrencyPositive();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();

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
        m_jMoneyEuros.setBounds(120, 4, 190, 30);

        m_jTendered.setFont(KALCFonts.DEFAULTFONTBOLD);
        jPanel4.add(m_jTendered);
        m_jTendered.setBounds(390, 10, 130, 25);

        jPanel5.add(jPanel4, java.awt.BorderLayout.NORTH);

        add(jPanel5, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel3.setLayout(new java.awt.BorderLayout());
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
    private javax.swing.JLabel m_jMoneyEuros;
    private ke.kalc.editor.JEditorCurrencyPositive m_jTendered;
    // End of variables declaration//GEN-END:variables

}
