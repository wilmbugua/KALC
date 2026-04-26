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


package uk.kalc.pos.util;

import java.awt.Image;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.ImageIcon;
import uk.kalc.pos.forms.AppConfig;
import uk.kalc.pos.forms.AppView;
import uk.kalc.pos.datalogic.DataLogicSystem;


public class ReportUtils {
         
    private ReportUtils() {
    }
    
    /**
     *
     * @param printername
     * @return
     */
    public static PrintService getPrintService(String printername) {
        
        // Initalize print service
        
        if (printername == null) {
            return PrintServiceLookup.lookupDefaultPrintService();       
        } else {
            
            if ("(Show dialog)".equals(printername)) {
                return null; // null means "you have to show the print dialog"
            } else if ("(Default)".equals(printername)) {
                return PrintServiceLookup.lookupDefaultPrintService(); 
            } else {
                PrintService[] pservices = 
                        PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PRINTABLE , null);
                for (PrintService s : pservices) {    
                    if (printername.equals(s.getName())) {
                        return s;
                    }
                }
                return PrintServiceLookup.lookupDefaultPrintService();       
            }                
        }                 
    }
    
    /**
     *
     * @return
     */
    public static String[] getPrintNames() {
        PrintService[] pservices = 
                PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PRINTABLE , null);
        
        String printers[] = new String[pservices.length];
        for (int i = 0; i < pservices.length; i++) {    
            printers[i] = pservices[i].getName();
        }
        
        return printers;
    }

    /**
     *
     * @return
     */
    public static Image getReportLogo() {
        ImageIcon img;
        
        img = new javax.swing.ImageIcon(ReportUtils.class.getResource("/uk/KALC/fixedimages/reportlogo.png"));            
        
        return img.getImage();
    }

}
