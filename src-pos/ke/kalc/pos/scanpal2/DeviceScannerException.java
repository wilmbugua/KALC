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


package ke.kalc.pos.scanpal2;

public class DeviceScannerException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>DeviceScannerException</code> without detail message.
     */
    public DeviceScannerException() {
    }
    
    
    /**
     * Constructs an instance of <code>DeviceScannerException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public DeviceScannerException(String msg) {
        super(msg);
    }

    /**
     *
     * @param msg
     * @param cause
     */
    public DeviceScannerException(String msg, Throwable cause) {
        super(msg, cause);
    }    

    /**
     *
     * @param cause
     */
    public DeviceScannerException(Throwable cause) {
        super(cause);
    }       
}
