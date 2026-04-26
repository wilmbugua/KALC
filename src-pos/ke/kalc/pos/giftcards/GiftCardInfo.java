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


package ke.kalc.pos.giftcards;

import java.math.BigDecimal;

/**
 *
 * @author John Lewis
 */
public class GiftCardInfo {

    private String openingValue = "0.00";
    private String remainingValue = "0.00";
    private String redeemedValue = "0.00";

    public GiftCardInfo() {

    }

    public GiftCardInfo(String openingValue, String remainingValue, String redeemedValue) {
        this.openingValue = openingValue;
        this.remainingValue = remainingValue;
        this.redeemedValue = redeemedValue;
    }

    public String getOpeningValue() {
        return openingValue;
    }

    public void setOpeningValue(String openingValue) {
        this.openingValue = openingValue;
    }

    public String getRemainingValue() {
        return remainingValue;
    }

    public void setRemainingValue(String remainingValue) {
        this.remainingValue = remainingValue;
    }

    public String getRedeemedValue() {
        return redeemedValue;
    }

    public void setRedeemedValue(String redeemedValue) {
        this.redeemedValue = redeemedValue;
    }

    public void updateCardTransaction(String redeemedValue) {
        setRemainingValue((new BigDecimal(getRemainingValue())).subtract(new BigDecimal(redeemedValue)).toString());
        setRedeemedValue((new BigDecimal(getRedeemedValue())).add(new BigDecimal(redeemedValue)).toString());
    }

    public Boolean checkValue(String value) {
        return (new BigDecimal(getRemainingValue()).compareTo(new BigDecimal(value))) == -1;
    }

}
