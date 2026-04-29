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

public class DocumentLoaderBasic implements DocumentLoader {
    
    /**
     *
     */
    public static final DocumentLoader INSTANCE = new DocumentLoaderBasic();
    
    /** Creates a new instance of DocumentLoaderBasic */
    private DocumentLoaderBasic() {
    }
    
    /**
     *
     * @param key
     * @return
     */
    public Object getValue(Object key) {
        return key;
    }

    /**
     *
     * @param value
     * @return
     */
    public Object getKey(Object value) {
        return value;
    }
}
