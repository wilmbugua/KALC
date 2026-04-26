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

public interface DisplayAnimator {
    
    /**
     *
     * @param i
     */
    public void setTiming(int i);

    /**
     *
     * @return
     */
    public String getLine1();

    /**
     *
     * @return
     */
    public String getLine2();
}
