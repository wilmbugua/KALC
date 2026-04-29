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

public class ScriptException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>ScriptException</code> without detail message.
     */
    public ScriptException() {
    }
    
    
    /**
     * Constructs an instance of <code>ScriptException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ScriptException(String msg) {
        super(msg);
    }

    /**
     *
     * @param msg
     * @param cause
     */
    public ScriptException(String msg, Throwable cause) {
        super(msg, cause);
    }
        
}
