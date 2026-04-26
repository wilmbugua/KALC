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

/**
 *
 *   
 */
public interface SentenceExec {
    
    /**
     *
     * @return
     * @throws BasicException
     */
    public int exec() throws BasicException;

    /**
     *
     * @param params
     * @return
     * @throws BasicException
     */
    public int exec(Object params) throws BasicException;

    /**
     *
     * @param params
     * @return
     * @throws BasicException
     */
    public int exec(Object... params) throws BasicException;
}
