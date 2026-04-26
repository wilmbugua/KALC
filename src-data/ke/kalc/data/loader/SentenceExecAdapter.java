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
public abstract class SentenceExecAdapter implements SentenceExec {
    
    /**
     *
     * @return
     * @throws BasicException
     */
    public int exec() throws BasicException {
        return exec((Object) null);
    }

    /**
     *
     * @param params
     * @return
     * @throws BasicException
     */
    public int exec(Object... params) throws BasicException {
        return exec((Object) params);
    }

    /**
     *
     * @param params
     * @return
     * @throws BasicException
     */
    public abstract int exec(Object params) throws BasicException;    
}
