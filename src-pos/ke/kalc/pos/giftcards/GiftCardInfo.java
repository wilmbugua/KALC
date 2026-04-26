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
