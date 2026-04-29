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

public class SerializerReadDate implements SerializerRead {

    /**
     *
     */
    public static final SerializerRead INSTANCE = new SerializerReadDate();

    /** Creates a new instance of SerializerReadImage */
    private SerializerReadDate() {
    }

    /**
     *
     * @param dr
     * @return
     * @throws BasicException
     */
    @Override
    public Object readValues(DataRead dr) throws BasicException {
        return Datas.TIMESTAMP.getValue(dr, 1);
    }
}
