/*
**    KALC POS  - Professional Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous Openbravo POS related works   
**
**    https://www.KALC.co.uk
**   
**
*/


package ke.kalc.pos.printer.escpos;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 *   
 */
public abstract class PrinterWritter {
    
    private boolean initialized = false;

    private ExecutorService exec;
    
    /**
     *
     */
    public PrinterWritter() {
        exec = Executors.newSingleThreadExecutor();
    }
    
    /**
     *
     * @param data
     */
    protected abstract void internalWrite(byte[] data);

    /**
     *
     */
    protected abstract void internalFlush();

    /**
     *
     */
    protected abstract void internalClose();
    
    /**
     *
     * @param data
     */
    public void init(final byte[] data) {
        if (!initialized) {
            write(data);
            initialized = true;
        }
    }
       
    /**
     *
     * @param sValue
     */
    public void write(String sValue) {
        write(sValue.getBytes());
    }

    /**
     *
     * @param data
     */
    public void write(final byte[] data) {
        exec.execute(new Runnable() {
            @Override
            public void run() {
                internalWrite(data);
            }
        });
    }
    
    /**
     *
     */
    public void flush() {
        exec.execute(new Runnable() {
            @Override
            public void run() {
                internalFlush();
            }
        });
    }
    
    /**
     *
     */
    public void close() {
        exec.execute(new Runnable() {
            @Override
            public void run() {
                internalClose();
            }
        });
        exec.shutdown();
    }
}
