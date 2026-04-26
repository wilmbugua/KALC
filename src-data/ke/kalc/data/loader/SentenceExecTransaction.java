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


public abstract class SentenceExecTransaction implements SentenceExec {
    
    private Session m_s;
    
    /**
     *
     * @param s
     */
    public SentenceExecTransaction(Session s) {
        m_s = s;
    }
    
    /**
     *
     * @return
     * @throws BasicException
     */
    public final int exec() throws BasicException {
        return exec((Object) null);
    }

    /**
     *
     * @param params
     * @return
     * @throws BasicException
     */
    @Override
    public final int exec(Object... params) throws BasicException {
        return exec((Object) params);
    }

    /**
     *
     * @param params
     * @return
     * @throws BasicException
     */
    @Override
    public final int exec(final Object params) throws BasicException {
        
        Transaction<Integer> t = new Transaction<Integer>(m_s) {
            @Override
            public Integer transact() throws BasicException{
                return execInTransaction(params);
            }
        };
        
        return t.execute();
    }
    
    /**
     *
     * @param params
     * @return
     * @throws BasicException
     */
    protected abstract int execInTransaction(Object params) throws BasicException; 
}

