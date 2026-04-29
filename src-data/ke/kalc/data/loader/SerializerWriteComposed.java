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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import ke.kalc.basic.BasicException;
import java.math.*;

/**
 *
 *
 */
public class SerializerWriteComposed implements SerializerWrite {

    private List<SerializerWrite> serwrites = new ArrayList<SerializerWrite>();

    /**
     * Creates a new instance of SerializerWriteComposed
     */
    public SerializerWriteComposed() {
    }

    /**
     *
     * @param sw
     */
    public void add(SerializerWrite sw) {
        serwrites.add(sw);
    }

    /**
     *
     * @param dp
     * @param obj
     * @throws BasicException
     */
    public void writeValues(DataWrite dp, Object obj) throws BasicException {

        Object[] a = (Object[]) obj;
        DataWriteComposed dpc = new DataWriteComposed(dp);

        int i = 0;
        for (SerializerWrite sw : serwrites) {
            dpc.next();
            sw.writeValues(dpc, a[i++]);
        }
    }

    private static class DataWriteComposed implements DataWrite {

        private DataWrite dp;
        private int offset = 0;
        private int max = 0;

        public DataWriteComposed(DataWrite dp) {
            this.dp = dp;
        }

        public void next() {
            offset = max;
        }

        public void setBigDecimal(int paramIndex, BigDecimal bdValue) throws BasicException {
            dp.setBigDecimal(offset + paramIndex, bdValue);
            max = Math.max(max, offset + paramIndex);
        }

        public void setInt(int paramIndex, Integer iValue) throws BasicException {
            dp.setInt(offset + paramIndex, iValue);
            max = Math.max(max, offset + paramIndex);
        }

        public void setString(int paramIndex, String sValue) throws BasicException {
            dp.setString(offset + paramIndex, sValue);
            max = Math.max(max, offset + paramIndex);
        }

        public void setDouble(int paramIndex, Double dValue) throws BasicException {
            dp.setDouble(offset + paramIndex, dValue);
            max = Math.max(max, offset + paramIndex);
        }

        public void setBoolean(int paramIndex, Boolean bValue) throws BasicException {
            dp.setBoolean(offset + paramIndex, bValue);
            max = Math.max(max, offset + paramIndex);
        }

        public void setTimestamp(int paramIndex, Date dValue) throws BasicException {
            dp.setTimestamp(offset + paramIndex, dValue);
            max = Math.max(max, offset + paramIndex);
        }

        public void setBytes(int paramIndex, byte[] value) throws BasicException {
            dp.setBytes(offset + paramIndex, value);
            max = Math.max(max, offset + paramIndex);
        }

        public void setObject(int paramIndex, Object value) throws BasicException {
            dp.setObject(offset + paramIndex, value);
            max = Math.max(max, offset + paramIndex);
        }
    }

}
