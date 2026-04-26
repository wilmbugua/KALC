/*
**    KALC POS  - Open Source Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous Openbravo POS related works   
**
**    https://www.KALC.co.uk
**   
**    KALC POS is free software: you can redistribute it and/or modify
**    it under the terms of the GNU General Public License as published by
**    the Free Software Foundation, either version 3 of the License, or
**    (at your option) any later version.
**
**    KALC POS is distributed in the hope that it will be useful,
**    but WITHOUT ANY WARRANTY; without even the implied warranty of
**    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
**    GNU General Public License for more details.
**
**    You should have received a copy of the GNU General Public License
**    along with KALC POS.  If not, see <http://www.gnu.org/licenses/>
**
*/


package uk.kalc.pos.printer.escpos;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 *   
 */
public class PrinterWritterFile extends PrinterWritter {
    
    private String m_sFilePrinter;
    private OutputStream m_out;
    
    /**
     *
     * @param sFilePrinter
     */
    public PrinterWritterFile(String sFilePrinter) {
        m_sFilePrinter = sFilePrinter;
        m_out = null;
    }

    /**
     *
     * @param data
     */
    @Override
    protected void internalWrite(byte[] data) {
        try {  
            if (m_out == null) {
                m_out = new FileOutputStream(m_sFilePrinter);  // No poner append = true.
            }
            m_out.write(data);
        } catch (IOException e) {
            System.err.println(e);
        }    
    }
    
    /**
     *
     */
    @Override
    protected void internalFlush() {
        try {  
            if (m_out != null) {
                m_out.flush();
                m_out.close();
                m_out = null;
            }
        } catch (IOException e) {
            System.err.println(e);
        }    
    }
    
    /**
     *
     */
    @Override
    protected void internalClose() {
        try {  
            if (m_out != null) {
                m_out.flush();
                m_out.close();
                m_out = null;
            }
        } catch (IOException e) {
            System.err.println(e);
        }    
    }    
}
