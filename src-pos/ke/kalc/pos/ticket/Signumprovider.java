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


package ke.kalc.pos.ticket;

import java.util.HashSet;
import java.util.Set;

/**
 *
 *   
 */
public class Signumprovider {
    
    private Set m_positives = new HashSet();
    private Set m_negatives = new HashSet();
    
    /** Creates a new instance of Signumprovider */
    public Signumprovider() {
    }
    
    /**
     *
     * @param key
     */
    public void addPositive(Object key) {
        m_positives.add(key);
    }
    
    /**
     *
     * @param key
     */
    public void addNegative(Object key) {
        m_negatives.add(key);
    }
    
    /**
     *
     * @param key
     * @param value
     * @return
     */
    public Double correctSignum(Object key, Double value) {
        if (m_positives.contains(key)) {
            return value.doubleValue() < 0.0 ? Double.valueOf(-value.doubleValue()) : value;
        } else if (m_negatives.contains(key)) {
            return value.doubleValue() > 0.0 ? Double.valueOf(-value.doubleValue()) : value;
        } else {
            return value;
        }        
    }    
}
