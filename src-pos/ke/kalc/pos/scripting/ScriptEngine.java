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


package ke.kalc.pos.scripting;

public interface ScriptEngine {
    
    /**
     *
     * @param key
     * @param value
     */
    public void put(String key, Object value);

    /**
     *
     * @param key
     * @return
     */
    public Object get(String key);
    
    /**
     *
     * @param src
     * @return
     * @throws ScriptException
     */
    public Object eval(String src) throws ScriptException;
    
}
