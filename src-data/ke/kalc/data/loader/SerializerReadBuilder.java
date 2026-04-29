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


public class SerializerReadBuilder  implements SerializerRead {
    
    private SerializableBuilder m_sb;
    
    /** Creates a new instance of SerializerReadBuilder
     * @param sb */
    public SerializerReadBuilder(SerializableBuilder sb) {
        m_sb = sb;
    }
    
    /**
     *
     * @param dr
     * @return
     * @throws BasicException
     */
    public Object readValues(DataRead dr) throws BasicException {
        SerializableRead sr = m_sb.createNew();
        sr.readValues(dr);
        return sr;
    }
    
}
