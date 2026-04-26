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

import ke.kalc.pos.forms.LocalResource;

/**
 *
 *
 */
public class PaymentInfoMagcard extends PaymentInfo {

    protected double m_dTotal;
    protected String m_sTransactionID;
    protected String m_sAuthorization;
    protected String m_sErrorMessage;
    protected String m_sReturnMessage;
    protected String cardType = "magcard";

    private int pointsBurned = 0;

    protected String m_dCardName = null;

    /**
     * Creates a new instance of PaymentInfoMagcard
     *
     * @param sTransactionID
     * @param dTotal
     */
    public PaymentInfoMagcard(
            String sTransactionID,
            double dTotal) {

        m_sTransactionID = sTransactionID;
        m_dTotal = dTotal;
    }

    @Override
    public PaymentInfo copyPayment() {
        PaymentInfoMagcard p = new PaymentInfoMagcard(
                m_sTransactionID,
                m_dTotal);
        return p;
    }

    public void setCardType(String name) {
        cardType = name;
    }

    @Override
    public String getName() {
        return cardType;
    }

    @Override
    public String getDescription() {
        return LocalResource.getString("paymentdescription." + cardType);
    }

    @Override
    public double getTotal() {
        return m_dTotal;
    }

    public boolean isPaymentOK() {
        return m_sAuthorization != null;
    }

    @Override
    public String getCardName() {
        return m_dCardName;
    }

    @Override
    public String getTransactionID() {
        return null;
    }

    public void paymentOK(String sAuthorization, String sTransactionId, String sReturnMessage) {
        m_sAuthorization = sAuthorization;
        m_sTransactionID = sTransactionId;
        m_sReturnMessage = sReturnMessage;
        m_sErrorMessage = null;
    }

    public String printTransactionID() {
        return m_sTransactionID;
    }

    @Override
    public double getPaid() {
        return m_dTotal;
    }

    @Override
    public double getChange() {
        return 0.00;
    }

    @Override
    public double getTendered() {
        return 0.00;
    }

    @Override
    public void addToTotal(Double balance) {
        m_dTotal = m_dTotal + balance;
    }

    public void setReturnMessage(String returnMessage) {
        m_sReturnMessage = returnMessage;
    }

    public String getReturnMessage() {
        return m_sReturnMessage;
    }

    @Override
    public Boolean isCardPayment() {
        return true;
    }

}
