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


package ke.kalc.pos.panels;

import java.util.Enumeration;
import java.util.Iterator;

public class EnumerationIter implements Enumeration {
    
    private Iterator i;

    /**
     *
     * @param i
     */
    public EnumerationIter(Iterator i) {
        this.i = i;
    }
    @Override
    public boolean hasMoreElements() {
        return i.hasNext();
    }
    @Override
    public Object nextElement() {
        return i.next();
    } 
}
