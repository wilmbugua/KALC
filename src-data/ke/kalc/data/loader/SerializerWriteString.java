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
 */
public class SerializerWriteString implements SerializerWrite<String> {
    
    /**
     *
     */
    public static final SerializerWrite INSTANCE = new SerializerWriteString();
    
    /** Creates a new instance of SerializerWriteString */
    private SerializerWriteString() {
    }
    
    /**
     *
     * @param dp
     * @param obj
     * @throws BasicException
     */
    public void writeValues(DataWrite dp, String obj) throws BasicException {
        Datas.STRING.setValue(dp, 1, obj);
    }  
}
