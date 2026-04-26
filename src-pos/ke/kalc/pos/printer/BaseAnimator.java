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


package ke.kalc.pos.printer;


public abstract class BaseAnimator implements DisplayAnimator {
    
    /**
     *
     */
    protected String baseLine1;

    /**
     *
     */
    protected String baseLine2;

    /**
     *
     */
    protected String currentLine1;

    /**
     *
     */
    protected String currentLine2;

    /**
     *
     */
    public BaseAnimator() {
        baseLine1 = null;
        baseLine2 = null;
    }

    /**
     *
     * @param line1
     * @param line2
     */
    public BaseAnimator(String line1, String line2) {
        baseLine1 = line1;
        baseLine2 = line2;
    }

    /**
     *
     * @return
     */
    @Override
    public String getLine1() {
        return currentLine1;
    }

    /**
     *
     * @return
     */
    @Override
    public String getLine2() {
        return currentLine2;
    }
}
