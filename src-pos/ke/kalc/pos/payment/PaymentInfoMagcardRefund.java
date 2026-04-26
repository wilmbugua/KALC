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

/**
 *
 *   
 */
public class PaymentInfoMagcardRefund extends PaymentInfoMagcard {
    
    /** Creates a new instance of PaymentInfoMagcardRefund
     * @param dTotal
     * @param sTransactionID */
    public PaymentInfoMagcardRefund(String sTransactionID, double dTotal) {
       super(sTransactionID, dTotal);
    }
    
    
    /**
     *
     * @return
     */
    @Override
    public PaymentInfo copyPayment(){
        PaymentInfoMagcard p = new PaymentInfoMagcardRefund(m_sTransactionID, m_dTotal);
        p.m_sAuthorization = m_sAuthorization;
        p.m_sErrorMessage = m_sErrorMessage;
        return p;
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
    //    return "magcardrefund";
    return (cardType.equalsIgnoreCase("magcard"))?"magcardrefund":cardType;
    //    return cardType;
    }    
}
