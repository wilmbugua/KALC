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


package ke.kalc.pos.inventory;

import ke.kalc.pos.panels.ComboItemLocal;

public class MovementReason extends ComboItemLocal {

    // The numeric key indicates whether this is an increase or decrease in stock level
    // It is important to get the sign right.
    public static final MovementReason IN_REFUND = new MovementReason(+2, "stock.in.refund");  
  
    public static final MovementReason OUT_SALE = new MovementReason(-1, "stock.out.sale");
    /**
     *
     */
   
    private MovementReason(Integer iKey, String sKeyValue) {
        super(iKey, sKeyValue);
    }

    /**
     *
     * @return
     */
    public boolean isInput() {
        return m_iKey > 0;
    }

    /**
     *
     * @param d
     * @return
     */
    public Double samesignum(Double d) {

        if (d == null || m_iKey == null) {
            return d;
        } else if ((m_iKey > 0 && d < 0.0)
                || (m_iKey < 0 && d > 0.0)) {
            return -d;
        } else {
            return d;
        }
    }

    /**
     *
     * @param dBuyPrice
     * @param dSellPrice
     * @return
     */
    public Double getPrice(Double dBuyPrice, Double dSellPrice) {

        if (this == OUT_SALE || this == IN_REFUND) {
            return dSellPrice;
        } else {
            return null;
        }
    }
}
