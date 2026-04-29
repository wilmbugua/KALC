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


public class KeyGetterBuilder implements IKeyGetter {
  
    /**
     *
     */
    public final static IKeyGetter INSTANCE = new KeyGetterBuilder();
    
    /** Creates a new instance of KeyGetterBuilder */
    public KeyGetterBuilder() {
    }
    
    /**
     *
     * @param value
     * @return
     */
    public Object getKey(Object value) {
        return (value == null) 
            ? null
            : ((IKeyed) value).getKey();
    }   
}
