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


package ke.kalc.data.user;

import java.util.List;
import ke.kalc.basic.BasicException;

/**
 *
 *   
 */
public interface ListProvider {

    /**
     *
     * @return
     * @throws BasicException
     */
    public List loadData() throws BasicException;    

    /**
     *
     * @return
     * @throws BasicException
     */
    public List refreshData() throws BasicException;     
    
    public List setData(Object values) throws BasicException;
}
