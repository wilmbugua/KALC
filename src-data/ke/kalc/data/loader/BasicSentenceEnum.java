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
import ke.kalc.pos.forms.AppLocal;


public class BasicSentenceEnum implements SentenceEnum {
    
    BaseSentence sent;
    DataResultSet SRS;
    
    /** Creates a new instance of AbstractSentenceEnum
     * @param sent */
    public BasicSentenceEnum(BaseSentence sent) {
        this.sent = sent;
        this.SRS = null;
    }
    
    /**
     *
     * @throws BasicException
     */
    public void load() throws BasicException {
        load(null);
    }

    /**
     *
     * @param params
     * @throws BasicException
     */
    public void load(Object params) throws BasicException {
        SRS = sent.openExec(params);
    }

    /**
     *
     * @return
     * @throws BasicException
     */
    public Object getCurrent() throws BasicException {
        if (SRS == null) {
            throw new BasicException(AppLocal.getIntString("exception.nodataset"));
        } 
        
        return SRS.getCurrent();  
    }
    
    /**
     *
     * @return
     * @throws BasicException
     */
    public boolean next() throws BasicException {
        if (SRS == null) {
            throw new BasicException(AppLocal.getIntString("exception.nodataset"));
        } 
        
        if (SRS.next()) {
            return true;  
        } else {
            SRS.close();
            SRS = null;
            sent.closeExec();
            return false;
        }
    }
}
