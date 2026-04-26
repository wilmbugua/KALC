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


package ke.kalc.pos.util;

import ke.kalc.format.Formats;

/**
 *
 *   
 */
public class RoundUtils {
    
    /** Creates a new instance of DoubleUtils */
    private RoundUtils() {
    }
    
    /**
     *
     * @param dValue
     * @return
     */
    public static double round(double dValue) {
        double fractionMultiplier = Math.pow(10.0, Formats.getCurrencyDecimals());
        return Math.rint(dValue * fractionMultiplier) / fractionMultiplier;
    }
    
    /**
     *
     * @param d1
     * @param d2
     * @return
     */
    public static int compare(double d1, double d2) {
        
        return Double.compare(round(d1), round(d2));
    }

    /**
     *
     * @param value
     * @return
     */
    public static double getValue(Double value) {
        return value == null ? 0.0 : value.doubleValue();
    }
}
