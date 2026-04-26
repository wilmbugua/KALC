/*
**    KALC POS  - Open Source Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous Openbravo POS related works   
**
**    https://www.KALC.co.uk
**   
**    KALC POS is free software: you can redistribute it and/or modify
**    it under the terms of the GNU General Public License as published by
**    the Free Software Foundation, either version 3 of the License, or
**    (at your option) any later version.
**
**    KALC POS is distributed in the hope that it will be useful,
**    but WITHOUT ANY WARRANTY; without even the implied warranty of
**    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
**    GNU General Public License for more details.
**
**    You should have received a copy of the GNU General Public License
**    along with KALC POS.  If not, see <http://www.gnu.org/licenses/>
**
*/


package uk.kalc.data.loader;

import uk.kalc.basic.BasicException;
import java.math.*;
import uk.kalc.pos.forms.AppLocal;


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
