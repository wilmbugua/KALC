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


package ke.kalc.pos.scanpal2;

/**
 *
 *   
 */
public class ProductDownloaded {
    
    private String m_sCode;
    private double m_dQuantity;
    
    /**
     *
     */
    public ProductDownloaded() {
    }
    
    /**
     *
     * @param value
     */
    public void setCode(String value) {
        m_sCode = value;
    }

    /**
     *
     * @return
     */
    public String getCode() {
        return m_sCode;
    }

    /**
     *
     * @param value
     */
    public void setQuantity(double value) {
        m_dQuantity = value;
    }

    /**
     *
     * @return
     */
    public double getQuantity() {
        return m_dQuantity;
    }
}