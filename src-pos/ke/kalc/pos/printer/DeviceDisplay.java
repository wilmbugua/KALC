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

import javax.swing.JComponent;

/**
 *
 *   
 */
public interface DeviceDisplay {


    /**
     *
     * @return
     */
        public String getDisplayName();

    /**
     *
     * @return
     */
    public String getDisplayDescription();

    /**
     *
     * @return
     */
    public JComponent getDisplayComponent();
    
    // INTERFAZ VISOR

    /**
     *
     * @param animation
     * @param sLine1
     * @param sLine2
     */
        public void writeVisor(int animation, String sLine1, String sLine2);

    /**
     *
     * @param sLine1
     * @param sLine2
     */
    public void writeVisor(String sLine1, String sLine2);

    /**
     *
     */
    public void clearVisor();
}
