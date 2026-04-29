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


package ke.kalc.basic;

/**
 *
 *   
 */
public class BasicException extends java.lang.Exception {
    
    private int action = -1;
    /**
     * Creates a new instance of <code>DataException</code> without detail message.
     */
    public BasicException() {
    }

    /**
     *
     * @param msg
     */
    public BasicException(String msg) {
        super(msg);
    }
    
    /**
     *
     * @param msg
     * @param cause
     */
    public BasicException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    /**
     *
     * @param cause
     */
    public BasicException(Throwable cause) {
        super(cause);
    }
    
    
    public BasicException(String msg, int action){
         super(msg);
         this.action = action;
    }
    
    public int getAction(){
        return action;
    }
}
