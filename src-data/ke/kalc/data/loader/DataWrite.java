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


public interface DataWrite {

    public void setInt(int paramIndex, Integer iValue) throws BasicException;

    public void setString(int paramIndex, String sValue) throws BasicException;

    public void setDouble(int paramIndex, Double dValue) throws BasicException;

    public void setBoolean(int paramIndex, Boolean bValue) throws BasicException;

    public void setTimestamp(int paramIndex, java.util.Date dValue) throws BasicException;

    public void setBytes(int paramIndex, byte[] value) throws BasicException;

    public void setObject(int paramIndex, Object value) throws BasicException;
    
    public void setBigDecimal(int paramIndex, java.math.BigDecimal bdValue) throws BasicException;
}
