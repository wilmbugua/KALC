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

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import ke.kalc.basic.BasicException;

/**
 *
 *   
 */
public class SerializerReadClass implements SerializerRead {

    private final Class m_clazz;
    
    /** Creates a new instance of DefaultSerializerRead
     * @param clazz */
    public SerializerReadClass(Class clazz) {
        m_clazz = clazz;
    }
    
    /**
     *
     * @param dr
     * @return
     * @throws BasicException
     */
    @Override
    public Object readValues(DataRead dr) throws BasicException {
        try {
            SerializableRead sr = (SerializableRead) m_clazz.getDeclaredConstructor().newInstance();
            sr.readValues(dr);
            return sr;
        } catch (java.lang.InstantiationException | IllegalAccessException | ClassCastException eIns) {
            return null;
        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(SerializerReadClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
