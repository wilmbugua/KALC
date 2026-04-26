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
import java.awt.Frame;
import java.awt.Window;

public class JPaymentSelectRefund extends JPaymentSelect {

    /**
     * Creates new form JPaymentSelect
     *
     * @param parent
     * @param modal
     * @param o
     */
    protected JPaymentSelectRefund(java.awt.Frame parent, boolean modal, ComponentOrientation o) {
        super(parent, modal, o);
    }

    /**
     * Creates new form JPaymentSelect
     *
     * @param parent
     * @param modal
     * @param o
     */
    protected JPaymentSelectRefund(java.awt.Dialog parent, boolean modal, ComponentOrientation o) {
        super(parent, modal, o);
    }

    /**
     *
     * @param parent
     * @return
     */
    public static JPaymentSelect getDialog(Component parent) {

        Window window = getWindow(parent);

        if (window instanceof Frame) {
            return new JPaymentSelectRefund((Frame) window, true, parent.getComponentOrientation());
        } else {
            return new JPaymentSelectRefund((Dialog) window, true, parent.getComponentOrientation());
        }
    }

    /**
     *
     */
    @Override
    protected void addTabs() {

        addTabPayment(new JPaymentSelect.JPaymentCashRefundCreator());
        addTabPayment(new JPaymentSelect.JPaymentChequeRefundCreator());
        addTabPayment(new JPaymentSelect.JPaymentPaperRefundCreator());
        addTabPayment(new JPaymentSelect.JPaymentMagcardRefundCreator());
        addTabPayment(new JPaymentSelect.JPaymentMagcardCustom1RefundCreator());
        addTabPayment(new JPaymentSelect.JPaymentMagcardCustom2RefundCreator());
        addTabPayment(new JPaymentSelect.JPaymentMagcardCustom3RefundCreator());
        addTabPayment(new JPaymentSelect.JPaymentMagcardCustom4RefundCreator());
        addTabPayment(new JPaymentSelect.JPaymentMagcardCustom5RefundCreator());
        addTabPayment(new JPaymentSelect.JPaymentCustomRefundCreator());
        addTabPayment(new JPaymentSelect.JPaymentDebtRefundCreator());
        setHeaderVisible(false);
    }

    /**
     *
     * @param isPositive
     * @param isComplete
     */
    @Override
    protected void setStatusPanel(boolean isPositive, boolean isComplete) {

        setAddEnabled(isPositive && !isComplete);
        setOKEnabled(isComplete);
    }

    /**
     *
     * @param total
     * @return
     */
    @Override
    protected PaymentInfo getDefaultPayment(double total) {
        return new PaymentInfoTicket(total, "cashrefund");
    }
}
