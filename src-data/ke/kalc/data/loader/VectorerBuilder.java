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

import java.util.ArrayList;
import ke.kalc.basic.BasicException;
import java.math.*;

/**
 *
 *
 */
public abstract class VectorerBuilder implements Vectorer {

    /**
     * Creates a new instance of VectorerBuilder
     */
    public VectorerBuilder() {
    }

    /**
     *
     * @return @throws BasicException
     */
    public abstract String[] getHeaders() throws BasicException;

    /**
     *
     * @param obj
     * @return
     * @throws BasicException
     */
    public String[] getValues(Object obj) throws BasicException {

        SerializableToArray s2a = new SerializableToArray();
        ((SerializableWrite) obj).writeValues(s2a);
        return s2a.getValues();
    }

    private static class SerializableToArray implements DataWrite {

        private ArrayList m_aParams;

        /**
         * Creates a new instance of MetaParameter
         */
        public SerializableToArray() {
            m_aParams = new ArrayList();
        }

        public void setBigDecimal(int paramIndex, BigDecimal bdValue) throws BasicException {
            ensurePlace(paramIndex - 1);
            m_aParams.set(paramIndex - 1, bdValue.toString());
        
        }

        public void setDouble(int paramIndex, Double dValue) throws BasicException {
            ensurePlace(paramIndex - 1);
            m_aParams.set(paramIndex - 1, dValue.toString());
        }

        public void setBoolean(int paramIndex, Boolean bValue) throws BasicException {
            ensurePlace(paramIndex - 1);
            m_aParams.set(paramIndex - 1, bValue.toString());
        }

        public void setInt(int paramIndex, Integer iValue) throws BasicException {
            ensurePlace(paramIndex - 1);
            m_aParams.set(paramIndex - 1, iValue.toString());
        }

        public void setString(int paramIndex, String sValue) throws BasicException {
            ensurePlace(paramIndex - 1);
            m_aParams.set(paramIndex - 1, sValue);
        }

        public void setTimestamp(int paramIndex, java.util.Date dValue) throws BasicException {
            ensurePlace(paramIndex - 1);
            m_aParams.set(paramIndex - 1, dValue.toString());
        }
//        public void setBinaryStream(int paramIndex, java.io.InputStream in, int length) throws DataException {
//            ensurePlace(paramIndex -1);
//            // m_aParams.set(paramIndex - 1, value.toString()); // quiza un uuencode o algo asi
//        }

        public void setBytes(int paramIndex, byte[] value) throws BasicException {
            ensurePlace(paramIndex - 1);
            m_aParams.set(paramIndex - 1, value.toString()); // quiza un uuencode o algo asi
        }

        public void setObject(int paramIndex, Object value) throws BasicException {
            ensurePlace(paramIndex - 1);
            m_aParams.set(paramIndex - 1, value.toString());
        }

        private void ensurePlace(int i) {
            m_aParams.ensureCapacity(i);
            while (i >= m_aParams.size()) {
                m_aParams.add(null);
            }
        }

        public String[] getValues() {
            return (String[]) m_aParams.toArray(new String[m_aParams.size()]);
        }
    }
}
