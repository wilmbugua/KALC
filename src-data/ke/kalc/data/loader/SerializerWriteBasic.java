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
public class SerializerWriteBasic implements SerializerWrite<Object[]> {
    
    private Datas[] m_classes;

    /** Creates a new instance of SerializerWriteBasic
     * @param classes */
    public SerializerWriteBasic(Datas... classes) {
        m_classes = classes;
    }
    
    /**
     *
     * @param dp
     * @param obj
     * @throws BasicException
     */
    @Override
    public void writeValues(DataWrite dp, Object[] obj) throws BasicException {

        for (int i = 0; i < m_classes.length; i++) {
            m_classes[i].setValue(dp, i + 1, obj[i]);
        }
    }
    
}
