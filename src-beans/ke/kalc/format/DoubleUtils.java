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


package ke.kalc.format;


public class DoubleUtils {

    /**
     *
     * @param value
     * @return
     */
    public static double fixDecimals(Number value) {
        return Math.rint((value).doubleValue() * 1000000.0) / 1000000.0;
    }
}
