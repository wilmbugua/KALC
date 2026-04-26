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

/**
 *
 *   
 */
public class OSValidator {
   
    private static String OS = System.getProperty("os.name").toLowerCase();

    public OSValidator() {            
        }
        
    public static String getOS(){
      if (isWindows()) {
                    return("w");
		} else if (isMac()) {
                    return("m");
		} else if (isUnix()) {
                    return("l");
		} else if (isSolaris()) {
                    return("s");
		} else {
                    return("x");
		}
    }

    public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}

    public static boolean isMac() {
		return (OS.indexOf("mac") >= 0); 
	}

    public static boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
	}

    public static boolean isSolaris() {
		return (OS.indexOf("sunos") >= 0); 
	}      
}
