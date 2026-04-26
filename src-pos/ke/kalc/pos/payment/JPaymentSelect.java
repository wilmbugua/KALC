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

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import ke.kalc.commons.dialogs.JAlertPane;
import ke.kalc.globals.SystemProperty;
import ke.kalc.format.Formats;
import ke.kalc.pos.customers.CustomerInfoExt;
import ke.kalc.pos.forms.AppLocal;
import ke.kalc.pos.forms.AppView;
import ke.kalc.pos.datalogic.DataLogicSystem;
import ke.kalc.pos.loyalty.LoyaltyCard;
import ke.kalc.globals.IconFactory;
import ke.kalc.pos.forms.AppUser;
import ke.kalc.pos.forms.KALCFonts;

public abstract class JPaymentSelect extends javax.swing.JDialog implements JPaymentNotifier {

    private PaymentInfoList m_aPaymentInfo;
    private boolean printReceipt = !SystemProperty.RECEIPTPRINTOFF;
    private boolean accepted;
    private AppView app;
    private double m_dTotal;
    private double balance = 0.00;
    private double surcharge = 0.00;
    protected CustomerInfoExt customerext;
    private DataLogicSystem dlSystem;
    private final Map<String, JPaymentInterface> payments = new HashMap<>();
    private String m_sTransactionID;
    private Frame caller;
    protected LoyaltyCard loyaltyCard;

    protected JPaymentSelect(java.awt.Frame parent, boolean modal, ComponentOrientation o) {
        super(parent, modal);

        initComponents();

        m_jCardSurcharge.setVisible(SystemProperty.HANDLINGFEES);
        jHandingFees.setVisible(SystemProperty.HANDLINGFEES);
        m_jHandlingFee.setVisible(SystemProperty.HANDLINGFEES);

        setMinimumSize(new java.awt.Dimension(732, 500));
        setPreferredSize(new java.awt.Dimension(732, 500));

        caller = parent;
        this.applyComponentOrientation(o);
        getRootPane().setDefaultButton(m_jOK);

        m_jGiftReceipt.addActionListener((ActionEvent e) -> {
            if (m_jGiftReceipt.isSelected()) {
                m_jGiftReceipt.setText(AppLocal.getIntString("button.gift.receipt.on"));
            } else {
                m_jGiftReceipt.setText(AppLocal.getIntString("button.gift.receipt.off"));
            }
        });

        //sync the display at this point
        m_jCardSurcharge.addActionListener((ActionEvent e) -> {
            if (m_jCardSurcharge.isSelected()) {
                surcharge = balance * (SystemProperty.HANDLINGFEE / 100);
                m_jHandlingFee.setText(Formats.CURRENCY.formatValue(surcharge));
                m_jRemainingBalance.setText(Formats.CURRENCY.formatValue(balance + surcharge));
                if (m_jTabPayment.getSelectedComponent() instanceof JPaymentMagcard) {
                }

            } else {
                surcharge = 0.00;
                m_jHandlingFee.setText(Formats.CURRENCY.formatValue(surcharge));
                m_jRemainingBalance.setText(Formats.CURRENCY.formatValue(balance + surcharge));
            }
        });
    }

    protected JPaymentSelect(java.awt.Dialog parent, boolean modal, ComponentOrientation o) {
        super(parent, modal);
        initComponents();
        this.applyComponentOrientation(o);
    }

    public void init(AppView app) {
        this.app = app;
        dlSystem = (DataLogicSystem) app.getBean("ke.kalc.pos.datalogic.DataLogicSystem");
    }

    public boolean printReceipt() {
        return printReceipt;
    }

    public boolean isGiftReceiptRequired() {
        return m_jGiftReceipt.isSelected();
    }

    public Double getSurcharge() {
        if (m_jCardSurcharge.isSelected()) {
            return surcharge;
        } else {
            return 0.00;
        }
    }

    public List<PaymentInfo> getSelectedPayments() {
        return m_aPaymentInfo.getPayments();
    }

    public boolean showDialog(double total, CustomerInfoExt customerext, LoyaltyCard loyaltyCard) {

        this.customerext = customerext;
        this.loyaltyCard = loyaltyCard;

        m_aPaymentInfo = new PaymentInfoList();
        accepted = false;

        m_dTotal = total;

        m_jGiftReceipt.setSelected(false);
        m_jCardSurcharge.setSelected(false);
        m_jTotalEuros.setText(Formats.CURRENCY.formatValue(m_dTotal));

        //set up the original panel 
        if (m_jCardSurcharge.isSelected()) {
            surcharge = m_dTotal * (SystemProperty.HANDLINGFEE / 100);
            m_jHandlingFee.setText(Formats.CURRENCY.formatValue(surcharge));

        } else {
            surcharge = 0.00;
            m_jHandlingFee.setText(Formats.CURRENCY.formatValue(0.00));
            balance = total;
        }

        addTabs();
        if (m_jTabPayment.getTabCount() == 0) {
            m_aPaymentInfo.add(getDefaultPayment(total));
            accepted = true;
        } else {
            getRootPane().setDefaultButton(m_jOK);
            printState();
            Dimension thisDim = this.getSize();
            int x = (caller.getX() + (caller.getWidth() - thisDim.width) / 2);
            int y = (caller.getY() + (caller.getHeight() - thisDim.height) / 2);
            this.setLocation(x, y);
            setVisible(true);
        }

        // remove all tabs        
        m_jTabPayment.removeAll();

        return accepted;
    }

    protected abstract void addTabs();

    protected abstract void setStatusPanel(boolean isPositive, boolean isComplete);

    protected abstract PaymentInfo getDefaultPayment(double total);

    protected void setOKEnabled(boolean value) {
        m_jOK.setEnabled(value);
    }

    protected void setAddEnabled(boolean value) {
        m_jAddPayment.setEnabled(value);
        m_jAddPayment.setEnabled(true);
    }

    protected void addTabPayment(JPaymentCreator jpay) {

        if (AppUser.hasPermission(jpay.getKey())) {
            if (!jpay.getKey().equals("payment.loyalty") || (jpay.getKey().equals("payment.loyalty")
                    && SystemProperty.LOYALTYTYPE.equals("earnx"))) {

                JPaymentInterface jpayinterface = payments.get(jpay.getKey());
                if (jpayinterface == null) {
                    jpayinterface = jpay.createJPayment();
                    payments.put(jpay.getKey(), jpayinterface);
                }

                jpayinterface.getComponent().applyComponentOrientation(getComponentOrientation());

                m_jTabPayment.addTab(
                        AppLocal.getIntString(jpay.getLabelKey()),
                        (SystemProperty.PAYMENTICONS) ? IconFactory.getIcon(jpay.getIconKey()) : null,
                        jpayinterface.getComponent(loyaltyCard));

                if (!SystemProperty.PAYMENTICONS) {
                    m_jTabPayment.setFont(KALCFonts.DEFAULTFONT.deriveFont(20f));
                }
            }

            if (jpay.getKey().equals("payment.giftcard")) {
                JPaymentGiftCard jpayinterface = (JPaymentGiftCard) payments.get(jpay.getKey());
                jpayinterface.resetCardMap();
            }
        }
    }

    public interface JPaymentCreator {

        public JPaymentInterface createJPayment();

        public String getKey();

        public String getLabelKey();

        public String getIconKey();
    }

    public class JPaymentLoyaltyCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentLoyalty(JPaymentSelect.this);
        }

        @Override
        public String getKey() {
            return "payment.loyalty";
        }

        @Override
        public String getLabelKey() {
            return "tab.loyalty";
        }

        @Override
        public String getIconKey() {
            return "cheque.png";
        }
    }

    public class JPaymentGiftCardCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentGiftCard(JPaymentSelect.this);
        }

        @Override
        public String getKey() {
            return "payment.giftcard";
        }

        @Override
        public String getLabelKey() {
            return "tab.giftcard";
        }

        @Override
        public String getIconKey() {
            return "ccard.png";
        }

    }

    public class JPaymentCashCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentCashPos(JPaymentSelect.this, dlSystem);
        }

        @Override
        public String getKey() {
            return "payment.cash";
        }

        @Override
        public String getLabelKey() {
            return "tab.cash";
        }

        @Override
        public String getIconKey() {
            return "cash.png";
        }
    }

    public class JPaymentChequeCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentCheque(JPaymentSelect.this);
        }

        @Override
        public String getKey() {
            return "payment.cheque";
        }

        @Override
        public String getLabelKey() {
            return "tab.cheque";
        }

        @Override
        public String getIconKey() {
            return "cheque.png";
        }
    }

    public class JPaymentPaperCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentPaper(JPaymentSelect.this, "paperin");
        }

        @Override
        public String getKey() {
            return "payment.paper";
        }

        @Override
        public String getLabelKey() {
            return "tab.paper";
        }

        @Override
        public String getIconKey() {
            return "voucher.png";
        }
    }

    public class JPaymentCustomCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentCustom(JPaymentSelect.this, "customin", dlSystem);
        }

        @Override
        public String getKey() {
            return "payment.custom";
        }

        @Override
        public String getLabelKey() {
            return "tab.custom";
        }

        @Override
        public String getIconKey() {
            return "voucher.png";
        }
    }

    public class JPaymentMagcardCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentMagcard(app, JPaymentSelect.this);
        }

        @Override
        public String getKey() {
            return "payment.magcard";
        }

        @Override
        public String getLabelKey() {
            return "tab.magcard";
        }

        @Override
        public String getIconKey() {
            return "ccard.png";
        }
    }

    public class JPaymentMagcardCustom1Creator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentMagcard(app, JPaymentSelect.this, "cmagcard1");
        }

        @Override
        public String getKey() {
            return "payment.cmagcard1";
        }

        @Override
        public String getLabelKey() {
            return "tab.cmagcard1";
        }

        @Override
        public String getIconKey() {
            return "ccard.png";
        }
    }

    public class JPaymentMagcardCustom2Creator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentMagcard(app, JPaymentSelect.this, "cmagcard2");
        }

        @Override
        public String getKey() {
            return "payment.cmagcard2";
        }

        @Override
        public String getLabelKey() {
            return "tab.cmagcard2";
        }

        @Override
        public String getIconKey() {
            return "ccard.png";
        }
    }

    public class JPaymentMagcardCustom3Creator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentMagcard(app, JPaymentSelect.this, "cmagcard3");
        }

        @Override
        public String getKey() {
            return "payment.cmagcard3";
        }

        @Override
        public String getLabelKey() {
            return "tab.cmagcard3";
        }

        @Override
        public String getIconKey() {
            return "ccard.png";
        }
    }

    public class JPaymentMagcardCustom4Creator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentMagcard(app, JPaymentSelect.this, "cmagcard4");
        }

        @Override
        public String getKey() {
            return "payment.cmagcard4";
        }

        @Override
        public String getLabelKey() {
            return "tab.cmagcard4";
        }

        @Override
        public String getIconKey() {
            return "ccard.png";
        }
    }

    public class JPaymentMagcardCustom5Creator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentMagcard(app, JPaymentSelect.this, "cmagcard5");
        }

        @Override
        public String getKey() {
            return "payment.cmagcard5";
        }

        @Override
        public String getLabelKey() {
            return "tab.cmagcard5";
        }

        @Override
        public String getIconKey() {
            return "ccard.png";
        }
    }

    public class JPaymentFreeCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentFree(JPaymentSelect.this);
        }

        @Override
        public String getKey() {
            return "payment.free";
        }

        @Override
        public String getLabelKey() {
            return "tab.free";
        }

        @Override
        public String getIconKey() {
            return "wallet.png";
        }
    }

    public class JPaymentDebtCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentDebt(JPaymentSelect.this);
        }

        @Override
        public String getKey() {
            return "payment.debt";
        }

        @Override
        public String getLabelKey() {
            return "tab.debt";
        }

        @Override
        public String getIconKey() {
            return "customer.png";
        }
    }

    public class JPaymentCashRefundCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentRefund(JPaymentSelect.this, "cashrefund");
        }

        @Override
        public String getKey() {
            return "refund.cash";
        }

        @Override
        public String getLabelKey() {
            return "tab.cashrefund";
        }

        @Override
        public String getIconKey() {
            return "cash.png";
        }
    }

    public class JPaymentChequeRefundCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentRefund(JPaymentSelect.this, "chequerefund");
        }

        @Override
        public String getKey() {
            return "refund.cheque";
        }

        @Override
        public String getLabelKey() {
            return "tab.chequerefund";
        }

        @Override
        public String getIconKey() {
            return "cheque.png";
        }
    }

    public class JPaymentPaperRefundCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentRefund(JPaymentSelect.this, "paperout");
        }

        @Override
        public String getKey() {
            return "refund.paper";
        }

        @Override
        public String getLabelKey() {
            return "tab.paper";
        }

        @Override
        public String getIconKey() {
            return "voucher.png";
        }
    }

    public class JPaymentCustomRefundCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentRefund(JPaymentSelect.this, "customout");
        }

        @Override
        public String getKey() {
            return "refund.custom";
        }

        @Override
        public String getLabelKey() {
            return "tab.custom";
        }

        @Override
        public String getIconKey() {
            return "voucher.png";
        }
    }

    public class JPaymentMagcardRefundCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentMagcard(app, JPaymentSelect.this);
        }

        @Override
        public String getKey() {
            return "refund.magcard";
        }

        @Override
        public String getLabelKey() {
            return "tab.magcard";
        }

        @Override
        public String getIconKey() {
            return "ccard.png";
        }
    }

    public class JPaymentMagcardCustom1RefundCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentMagcard(app, JPaymentSelect.this, "cmagcard1refund");
        }

        @Override
        public String getKey() {
            return "refund.cmagcard1";
        }

        @Override
        public String getLabelKey() {
            return "tab.cmagcard1";
        }

        @Override
        public String getIconKey() {
            return "ccard.png";
        }
    }

    public class JPaymentMagcardCustom2RefundCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentMagcard(app, JPaymentSelect.this, "cmagcard2refund");
        }

        @Override
        public String getKey() {
            return "refund.cmagcard2";
        }

        @Override
        public String getLabelKey() {
            return "tab.cmagcard2";
        }

        @Override
        public String getIconKey() {
            return "ccard.png";
        }
    }

    public class JPaymentMagcardCustom3RefundCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentMagcard(app, JPaymentSelect.this, "cmagcard3refund");
        }

        @Override
        public String getKey() {
            return "refund.cmagcard3";
        }

        @Override
        public String getLabelKey() {
            return "tab.cmagcard3";
        }

        @Override
        public String getIconKey() {
            return "ccard.png";
        }
    }

    public class JPaymentMagcardCustom4RefundCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentMagcard(app, JPaymentSelect.this, "cmagcard4refund");
        }

        @Override
        public String getKey() {
            return "refund.cmagcard4";
        }

        @Override
        public String getLabelKey() {
            return "tab.cmagcard4";
        }

        @Override
        public String getIconKey() {
            return "ccard.png";
        }
    }

    public class JPaymentMagcardCustom5RefundCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentMagcard(app, JPaymentSelect.this, "cmagcard5refund");
        }

        @Override
        public String getKey() {
            return "refund.cmagcard5";
        }

        @Override
        public String getLabelKey() {
            return "tab.cmagcard5";
        }

        @Override
        public String getIconKey() {
            return "ccard.png";
        }
    }

    public class JPaymentBankCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentBank(JPaymentSelect.this);
        }

        @Override
        public String getKey() {
            return "payment.bank";
        }

        @Override
        public String getLabelKey() {
            return "tab.bank";
        }

        @Override
        public String getIconKey() {
            return "bank.png";
        }
    }

    public class JPaymentDebtRefundCreator implements JPaymentCreator {

        @Override
        public JPaymentInterface createJPayment() {
            return new JPaymentDebt(JPaymentSelect.this);
        }

        @Override
        public String getKey() {
            return "refund.debt";
        }

        @Override
        public String getLabelKey() {
            return "tab.debt";
        }

        @Override
        public String getIconKey() {
            return "customer.png";
        }
    }

    protected void setHeaderVisible(boolean value) {
        jPanel4.setVisible(value);
    }

    private void printState() {
        m_jButtonRemove.setEnabled(!m_aPaymentInfo.isEmpty());
        m_jTabPayment.setSelectedIndex(0);
        ((JPaymentInterface) m_jTabPayment.getSelectedComponent()).activate(customerext, m_dTotal - m_aPaymentInfo.getTotal(), m_sTransactionID);

        //sync the display
        if (m_jCardSurcharge.isSelected()) {
            surcharge = balance * (SystemProperty.HANDLINGFEE / 100);
            m_jHandlingFee.setText(Formats.CURRENCY.formatValue(surcharge));
            m_jRemainingBalance.setText(Formats.CURRENCY.formatValue(balance + surcharge));
        } else {
            surcharge = 0.00;
            m_jHandlingFee.setText(Formats.CURRENCY.formatValue(surcharge));
            m_jRemainingBalance.setText(Formats.CURRENCY.formatValue(balance + surcharge));
        }
    }

    protected static Window getWindow(Component parent) {
        if (parent == null) {
            return new JFrame();
        } else if (parent instanceof Frame || parent instanceof Dialog) {
            return (Window) parent;
        } else {
            return getWindow(parent.getParent());
        }
    }

    @Override
    public void setStatus(boolean isPositive, boolean isComplete) {
        setStatusPanel(isPositive, isComplete);
    }

    @Override
    public void setDefaultBtn(boolean value) {
        if (value) {
            getRootPane().setDefaultButton(m_jOK);
        } else {
            getRootPane().setDefaultButton(null);
        }
    }

    public void setTransactionID(String tID) {
        this.m_sTransactionID = tID;
    }

    public void setReceiptRequired(Boolean required) {
        printReceipt = required;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        m_jLblTotalEuros1 = new javax.swing.JLabel();
        m_jTotalEuros = new javax.swing.JLabel();
        jHandingFees = new javax.swing.JLabel();
        m_jHandlingFee = new javax.swing.JLabel();
        m_jLblRemainingEuros = new javax.swing.JLabel();
        m_jRemainingBalance = new javax.swing.JLabel();
        m_jAddPayment = new javax.swing.JButton();
        m_jButtonRemove = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        m_jTabPayment = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        m_jCardSurcharge = new javax.swing.JToggleButton();
        m_jGiftReceipt = new javax.swing.JToggleButton();
        m_jCancel = new javax.swing.JButton();
        m_jOK = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(AppLocal.getIntString("payment.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(732, 510));
        setPreferredSize(new java.awt.Dimension(732, 510));
        setResizable(false);

        m_jLblTotalEuros1.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(18f)
        );
        m_jLblTotalEuros1.setText(AppLocal.getIntString("label.totalcash"));
        jPanel4.add(m_jLblTotalEuros1);

        m_jTotalEuros.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(18f)
        );
        m_jTotalEuros.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jTotalEuros.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jTotalEuros.setOpaque(true);
        m_jTotalEuros.setPreferredSize(new java.awt.Dimension(125, 25));
        m_jTotalEuros.setRequestFocusEnabled(false);
        jPanel4.add(m_jTotalEuros);

        jHandingFees.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(18f));
        jHandingFees.setText(AppLocal.getIntString("label.cardhandling"));
        jPanel4.add(jHandingFees);

        m_jHandlingFee.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(18f)
        );
        m_jHandlingFee.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jHandlingFee.setText("0.00");
        m_jHandlingFee.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jHandlingFee.setOpaque(true);
        m_jHandlingFee.setPreferredSize(new java.awt.Dimension(75, 25));
        m_jHandlingFee.setRequestFocusEnabled(false);
        jPanel4.add(m_jHandlingFee);

        m_jLblRemainingEuros.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(18f)
        );
        m_jLblRemainingEuros.setText(AppLocal.getIntString("label.remainingcash"));
        jPanel4.add(m_jLblRemainingEuros);

        m_jRemainingBalance.setFont(KALCFonts.DEFAULTFONTBOLD.deriveFont(18f)
        );
        m_jRemainingBalance.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jRemainingBalance.setText("0.00");
        m_jRemainingBalance.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jRemainingBalance.setOpaque(true);
        m_jRemainingBalance.setPreferredSize(new java.awt.Dimension(125, 25));
        m_jRemainingBalance.setRequestFocusEnabled(false);
        jPanel4.add(m_jRemainingBalance);

        m_jAddPayment.setIcon(IconFactory.getIcon("btnplus.png"));
        m_jAddPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jAddPaymentActionPerformed(evt);
            }
        });
        jPanel4.add(m_jAddPayment);

        m_jButtonRemove.setIcon(IconFactory.getIcon("btnminus.png"));
        m_jButtonRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jButtonRemoveActionPerformed(evt);
            }
        });
        jPanel4.add(m_jButtonRemove);

        getContentPane().add(jPanel4, java.awt.BorderLayout.NORTH);

        jPanel3.setLayout(new java.awt.BorderLayout());

        m_jTabPayment.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        m_jTabPayment.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        m_jTabPayment.setFocusable(false);
        m_jTabPayment.setRequestFocusEnabled(false);
        m_jTabPayment.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                m_jTabPaymentStateChanged(evt);
            }
        });
        jPanel3.add(m_jTabPayment, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel7.setPreferredSize(new java.awt.Dimension(350, 75));

        m_jCardSurcharge.setFont(KALCFonts.DEFAULTBUTTONFONT);
        m_jCardSurcharge.setIcon(IconFactory.getIcon("percent.png"));
        m_jCardSurcharge.setText(AppLocal.getIntString("button.cardhandling"));
        m_jCardSurcharge.setFocusPainted(false);
        m_jCardSurcharge.setFocusTraversalPolicyProvider(true);
        m_jCardSurcharge.setFocusable(false);
        m_jCardSurcharge.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        m_jCardSurcharge.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jCardSurcharge.setMaximumSize(new java.awt.Dimension(165, 44));
        m_jCardSurcharge.setMinimumSize(new java.awt.Dimension(82, 44));
        m_jCardSurcharge.setPreferredSize(new java.awt.Dimension(165, 55));
        m_jCardSurcharge.setRequestFocusEnabled(false);
        m_jCardSurcharge.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel7.add(m_jCardSurcharge);

        m_jGiftReceipt.setFont(KALCFonts.DEFAULTBUTTONFONT);
        m_jGiftReceipt.setIcon(IconFactory.getIcon("printer24.png"));
        m_jGiftReceipt.setSelected(true);
        m_jGiftReceipt.setText(AppLocal.getIntString("button.gift.receipt.off"));
        m_jGiftReceipt.setFocusPainted(false);
        m_jGiftReceipt.setFocusTraversalPolicyProvider(true);
        m_jGiftReceipt.setFocusable(false);
        m_jGiftReceipt.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        m_jGiftReceipt.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jGiftReceipt.setMaximumSize(new java.awt.Dimension(170, 44));
        m_jGiftReceipt.setMinimumSize(new java.awt.Dimension(82, 44));
        m_jGiftReceipt.setPreferredSize(new java.awt.Dimension(165, 55));
        m_jGiftReceipt.setRequestFocusEnabled(false);
        m_jGiftReceipt.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel7.add(m_jGiftReceipt);

        m_jCancel.setFont(KALCFonts.DEFAULTBUTTONFONT);
        m_jCancel.setIcon(IconFactory.getIcon("cancel.png"));
        m_jCancel.setText(AppLocal.getIntString("button.cancel"));
        m_jCancel.setFocusPainted(false);
        m_jCancel.setFocusable(false);
        m_jCancel.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jCancel.setPreferredSize(new java.awt.Dimension(165, 55));
        m_jCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCancelActionPerformed(evt);
            }
        });
        jPanel7.add(m_jCancel);

        m_jOK.setFont(KALCFonts.DEFAULTBUTTONFONT);
        m_jOK.setIcon(IconFactory.getIcon("ok.png"));
        m_jOK.setText(AppLocal.getIntString("button.ok")); // NOI18N
        m_jOK.setFocusPainted(false);
        m_jOK.setFocusable(false);
        m_jOK.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jOK.setMaximumSize(new java.awt.Dimension(100, 44));
        m_jOK.setPreferredSize(new java.awt.Dimension(165, 55));
        m_jOK.setRequestFocusEnabled(false);
        m_jOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jOKActionPerformed(evt);
            }
        });
        jPanel7.add(m_jOK);

        jPanel5.add(jPanel7, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel5, java.awt.BorderLayout.SOUTH);

        setSize(new java.awt.Dimension(672, 497));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jButtonRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jButtonRemoveActionPerformed
        m_aPaymentInfo.removeLast();
        printState();
    }//GEN-LAST:event_m_jButtonRemoveActionPerformed

    private void m_jAddPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jAddPaymentActionPerformed
        PaymentInfo returnPayment = ((JPaymentInterface) m_jTabPayment.getSelectedComponent()).executePayment();
        if (returnPayment != null) {
            //reduce the balance by new payment
            balance = balance - returnPayment.getTendered();
            m_aPaymentInfo.add(returnPayment);
            printState();
        }
    }//GEN-LAST:event_m_jAddPaymentActionPerformed

    private void m_jTabPaymentStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_m_jTabPaymentStateChanged
        if (m_jTabPayment.getSelectedComponent() instanceof JPaymentMagcard) {
            ((JPaymentInterface) m_jTabPayment.getSelectedComponent()).activate(customerext,
                    (m_jCardSurcharge.isSelected()) ? m_dTotal + surcharge - m_aPaymentInfo.getTotal() : m_dTotal - m_aPaymentInfo.getTotal(),
                    m_sTransactionID);
            return;
        }

        if (m_jTabPayment.getSelectedComponent() != null) {
            ((JPaymentInterface) m_jTabPayment.getSelectedComponent()).activate(customerext,
                    m_dTotal - m_aPaymentInfo.getTotal(),
                    m_sTransactionID);
        }

    }//GEN-LAST:event_m_jTabPaymentStateChanged

    private void m_jOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jOKActionPerformed
        PaymentInfo returnPayment = ((JPaymentInterface) m_jTabPayment.getSelectedComponent()).executePayment();
        if (m_jTabPayment.getSelectedComponent() instanceof JPaymentMagcard) {
            surcharge = balance * (SystemProperty.HANDLINGFEE / 100);
            returnPayment.addToTotal(surcharge);
        } else {
            surcharge = 0.00;
        }

        if (returnPayment != null) {
            if (returnPayment.getChange() > SystemProperty.CHANGELIMIT && SystemProperty.ENABLECHANGELIMIT && returnPayment.getTotal() > 0.00) {
                Toolkit.getDefaultToolkit().beep();
                JAlertPane.messageBox(JAlertPane.WARNING, AppLocal.getIntString("message.largechange"), 16,
                        new Dimension(100, 50), JAlertPane.OK_OPTION);
                addTabs();
            } else if (returnPayment != null) {
                m_aPaymentInfo.add(returnPayment);
                // always ensure cash is last in the list for payment
                m_aPaymentInfo.sortPayments(m_dTotal);
                accepted = true;
                dispose();
            }
        }
    }//GEN-LAST:event_m_jOKActionPerformed

    private void m_jCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jCancelActionPerformed
        dispose();

    }//GEN-LAST:event_m_jCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jHandingFees;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JButton m_jAddPayment;
    private javax.swing.JButton m_jButtonRemove;
    private javax.swing.JButton m_jCancel;
    private javax.swing.JToggleButton m_jCardSurcharge;
    private javax.swing.JToggleButton m_jGiftReceipt;
    private javax.swing.JLabel m_jHandlingFee;
    private javax.swing.JLabel m_jLblRemainingEuros;
    private javax.swing.JLabel m_jLblTotalEuros1;
    private javax.swing.JButton m_jOK;
    private javax.swing.JLabel m_jRemainingBalance;
    private javax.swing.JTabbedPane m_jTabPayment;
    private javax.swing.JLabel m_jTotalEuros;
    // End of variables declaration//GEN-END:variables

}
