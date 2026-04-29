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


package ke.kalc.pos.forms;

public class BeanFactoryException extends java.lang.RuntimeException {
    
    /**
     * Creates a new instance of <code>BeanFactoryException</code> without detail message.
     */
    public BeanFactoryException() {
    }
    
    
    /**
     * Constructs an instance of <code>BeanFactoryException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public BeanFactoryException(String msg) {
        super(msg);
    }
    
    /**
     *
     * @param e
     */
    public BeanFactoryException(Throwable e) {
        super(e);
    }    
}
