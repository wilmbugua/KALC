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


package ke.kalc.data.loader;

import ke.kalc.basic.BasicException;

/**
 *
 *   
 */
public class SerializerWriteBuilder implements SerializerWrite<SerializableWrite> {
    
    /**
     *
     */
    public static final SerializerWrite INSTANCE = new SerializerWriteBuilder();
    
    /** Creates a new instance of SerializerWriteBuilder */
    private SerializerWriteBuilder() {
    }
    
    /**
     *
     * @param dp
     * @param obj
     * @throws BasicException
     */
    public void writeValues(DataWrite dp, SerializableWrite obj) throws BasicException {
        obj.writeValues(dp);
    }
}
