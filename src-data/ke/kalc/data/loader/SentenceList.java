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

import java.util.List;
import ke.kalc.basic.BasicException;

/**
 *
 *   
 */
public interface SentenceList {
    
    /**
     *
     * @return
     * @throws BasicException
     */
    public List list() throws BasicException;

    /**
     *
     * @param params
     * @return
     * @throws BasicException
     */
    public List list(Object... params) throws BasicException;

    /**
     *
     * @param params
     * @return
     * @throws BasicException
     */
    public List list(Object params) throws BasicException;
    
    /**
     *
     * @param offset
     * @param length
     * @return
     * @throws BasicException
     */
    public List listPage(int offset, int length) throws BasicException;

    /**
     *
     * @param params
     * @param offset
     * @param length
     * @return
     * @throws BasicException
     */
    public List listPage(Object params, int offset, int length) throws BasicException;    
}
