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
import ke.kalc.globals.SystemProperty;

public class JPaymentSelectReceipt extends JPaymentSelect {

    /**
     * Creates new form JPaymentSelect
     *
     * @param parent
     * @param modal
     * @param o
     */
    protected JPaymentSelectReceipt(java.awt.Frame parent, boolean modal, ComponentOrientation o) {
        super(parent, modal, o);
    }

    /**
     * Creates new form JPaymentSelect
     *
     * @param parent
     * @param o
     * @param modal
     */
    protected JPaymentSelectReceipt(java.awt.Dialog parent, boolean modal, ComponentOrientation o) {
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
            return new JPaymentSelectReceipt((Frame) window, true, parent.getComponentOrientation());
        } else {
            return new JPaymentSelectReceipt((Dialog) window, true, parent.getComponentOrientation());
        }
    }

    /**
     *
     */
    @Override
    protected void addTabs() {

        addTabPayment(new JPaymentSelect.JPaymentCashCreator());
        addTabPayment(new JPaymentSelect.JPaymentMagcardCreator());
        addTabPayment(new JPaymentSelect.JPaymentMagcardCustom1Creator());
        addTabPayment(new JPaymentSelect.JPaymentMagcardCustom2Creator());
        addTabPayment(new JPaymentSelect.JPaymentMagcardCustom3Creator());
        addTabPayment(new JPaymentSelect.JPaymentMagcardCustom4Creator());
        addTabPayment(new JPaymentSelect.JPaymentMagcardCustom5Creator());
        addTabPayment(new JPaymentSelect.JPaymentChequeCreator());
        addTabPayment(new JPaymentSelect.JPaymentPaperCreator());

        addTabPayment(new JPaymentSelect.JPaymentFreeCreator());
        if (customerext != null && customerext.getMaxDebt() != 0.00) {
            addTabPayment(new JPaymentSelect.JPaymentDebtCreator());
        }
        addTabPayment(new JPaymentSelect.JPaymentBankCreator());
        addTabPayment(new JPaymentSelect.JPaymentCustomCreator());

        if (SystemProperty.LOYALTYENABLED && SystemProperty.LOYALTYTYPE.equals("earnx") && loyaltyCard != null) {
            addTabPayment(new JPaymentSelect.JPaymentLoyaltyCreator());
        }
        if (SystemProperty.GIFTCARDSENABLED) {
            addTabPayment(new JPaymentSelect.JPaymentGiftCardCreator());
        }
        setHeaderVisible(true);
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
        return new PaymentInfoCash_original(total, total);
    }
}
