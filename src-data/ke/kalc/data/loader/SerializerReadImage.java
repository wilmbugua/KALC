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
public class SerializerReadImage implements SerializerRead {
    
    /**
     *
     */
    public static final SerializerRead INSTANCE = new SerializerReadImage();
    
    /** Creates a new instance of SerializerReadImage */
    private SerializerReadImage() {
    }
    
    /**
     *
     * @param dr
     * @return
     * @throws BasicException
     */
    public Object readValues(DataRead dr) throws BasicException {
        return Datas.IMAGE.getValue(dr,1);
    }    
    
}
