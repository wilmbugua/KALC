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


public class SerializerWriteParams  implements SerializerWrite<DataParams>{
    
    /**
     *
     */
    public static final SerializerWrite INSTANCE = new SerializerWriteParams();
    
    /**
     *
     * @param dp
     * @param obj
     * @throws BasicException
     */
    @Override
    public void writeValues(DataWrite dp, DataParams obj) throws BasicException {
        obj.setDataWrite(dp);
        obj.writeValues();
        obj.setDataWrite(null);
    }     
}
