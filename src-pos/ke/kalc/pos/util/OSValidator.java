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


package ke.kalc.pos.util;

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
