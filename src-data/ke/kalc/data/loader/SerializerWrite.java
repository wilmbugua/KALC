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


package ke.kalc.data.loader;

import ke.kalc.basic.BasicException;

/**
 *
 *   
 * @param <T>
 */
public interface SerializerWrite<T> {
    
    /**
     *
     * @param dp
     * @param obj
     * @throws BasicException
     */
    public void writeValues(DataWrite dp, T obj) throws BasicException;   
}
