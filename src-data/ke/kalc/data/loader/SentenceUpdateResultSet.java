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
import java.math.*;
import ke.kalc.pos.forms.AppLocal;


public class SentenceUpdateResultSet implements DataResultSet {

    private int m_iUpdateCount;

    /**
     * Creates a new instance of UpdateResultSet
     *
     * @param iUpdateCount
     */
    public SentenceUpdateResultSet(int iUpdateCount) {
        m_iUpdateCount = iUpdateCount;
    }

    /**
     *
     * @param columnIndex
     * @return
     * @throws BasicException
     */
    public Integer getInt(int columnIndex) throws BasicException {
        throw new BasicException(AppLocal.getIntString("exception.nodataset"));
    }

    /**
     *
     * @param columnIndex
     * @return
     * @throws BasicException
     */
    public String getString(int columnIndex) throws BasicException {
        throw new BasicException(AppLocal.getIntString("exception.nodataset"));
    }

    /**
     *
     * @param columnIndex
     * @return
     * @throws BasicException
     */
    public BigDecimal getBigDecimal(int columnIndex) throws BasicException {
        throw new BasicException(AppLocal.getIntString("exception.nodataset"));
    }

    /**
     *
     * @param columnIndex
     * @return
     * @throws BasicException
     */
    public Double getDouble(int columnIndex) throws BasicException {
        throw new BasicException(AppLocal.getIntString("exception.nodataset"));
    }

    /**
     *
     * @param columnIndex
     * @return
     * @throws BasicException
     */
    public Boolean getBoolean(int columnIndex) throws BasicException {
        throw new BasicException(AppLocal.getIntString("exception.nodataset"));
    }

    /**
     *
     * @param columnIndex
     * @return
     * @throws BasicException
     */
    public java.util.Date getTimestamp(int columnIndex) throws BasicException {
        throw new BasicException(AppLocal.getIntString("exception.nodataset"));
    }

    //public java.io.InputStream getBinaryStream(int columnIndex) throws DataException;
    /**
     *
     * @param columnIndex
     * @return
     * @throws BasicException
     */
    public byte[] getBytes(int columnIndex) throws BasicException {
        throw new BasicException(AppLocal.getIntString("exception.nodataset"));
    }

    /**
     *
     * @param columnIndex
     * @return
     * @throws BasicException
     */
    public Object getObject(int columnIndex) throws BasicException {
        throw new BasicException(AppLocal.getIntString("exception.nodataset"));
    }

//    public int getColumnCount() throws DataException;
    /**
     *
     * @return @throws BasicException
     */
    public DataField[] getDataField() throws BasicException {
        throw new BasicException(AppLocal.getIntString("exception.nodataset"));
    }

    /**
     *
     * @return @throws BasicException
     */
    public Object getCurrent() throws BasicException {
        throw new BasicException(AppLocal.getIntString("exception.nodataset"));
    }

    /**
     *
     * @return @throws BasicException
     */
    public boolean next() throws BasicException {
        throw new BasicException(AppLocal.getIntString("exception.nodataset"));
    }

    /**
     *
     * @throws BasicException
     */
    public void close() throws BasicException {
    }

    /**
     *
     * @return @throws BasicException
     */
    public int updateCount() throws BasicException {
        return m_iUpdateCount;
    }
}
