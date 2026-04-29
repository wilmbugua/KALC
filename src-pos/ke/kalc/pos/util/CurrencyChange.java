/*
**    KALC POS  - Professional Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous KALC POS related works   
**
**    https://www.kalc.co.ke
**   
**
*/


package ke.kalc.pos.util;

/**
 *
 *   
 */
public final class CurrencyChange {

    /**
     *
     */
    public final static double EUROS_CHANGE = 166.386;

    private CurrencyChange() {
    }
  
    /**
     *
     * @param dEuros
     * @return
     */
    public static double changeEurosToPts(double dEuros) {        
        return Math.rint(dEuros * EUROS_CHANGE);
    }

    /**
     *
     * @param dPts
     * @return
     */
    public static double changePtsToEuros(double dPts) {        
        return Math.rint(100.0 * dPts / EUROS_CHANGE) / 100.0;
    }   
}
