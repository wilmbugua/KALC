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

import java.sql.SQLException;
import ke.kalc.basic.BasicException;


public abstract class Transaction<T> {
    
    private Session s;
    
    /** Creates a new instance of Transaction
     * @param s */
    public Transaction(Session s) {
        this.s = s;
    }
    
    /**
     *
     * @return
     * @throws BasicException
     */
    public final T execute() throws BasicException {
        
        if (s.isTransaction()) {
            return transact();
        } else {
            try {
                try {    
                    s.begin();
                    T result = transact();
                    s.commit();
                    return result;
                } catch (BasicException e) {
                    s.rollback();
                    throw e;
                }
            } catch (SQLException eSQL) {
                throw new BasicException("Transaction error", eSQL);
            }
        }
    }
    
    /**
     *
     * @return
     * @throws BasicException
     */
    protected abstract T transact() throws BasicException;
}
