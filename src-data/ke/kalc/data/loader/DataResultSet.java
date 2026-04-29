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
public interface DataResultSet extends DataRead {
    
    /**
     *
     * @return
     * @throws BasicException
     */
    public Object getCurrent() throws BasicException;

    /**
     *
     * @return
     * @throws BasicException
     */
    public boolean next() throws BasicException;    

    /**
     *
     * @throws BasicException
     */
    public void close() throws BasicException;
    
    /**
     *
     * @return
     * @throws BasicException
     */
    public int updateCount() throws BasicException;           
}
