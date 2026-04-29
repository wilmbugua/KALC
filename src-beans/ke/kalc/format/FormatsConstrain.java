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


package ke.kalc.format;

import java.text.ParseException;

/**
 *
 *   
 */
public abstract class FormatsConstrain {
   
//    public final static FormatsConstrain NOTNULL = new FormatsConstrainNOTNULL();
    
    /**
     *
     * @param value
     * @return
     * @throws ParseException
     */
        
    public abstract Object check(Object value) throws ParseException;

    /**
     *
     */
    public FormatsConstrain() {
    }
    
      
}
