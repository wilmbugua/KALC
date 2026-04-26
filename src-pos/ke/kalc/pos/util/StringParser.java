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
public class StringParser {
    
    private int currentPosition;
    private int maxPosition;
    private String str;
    
    /** Creates a new instance of StringParser
     * @param str */
    public StringParser(String str) {
        this.str = str;
        currentPosition = 0;
        maxPosition = str == null ? 0 : str.length();
    }
    
    /**
     *
     * @param c
     * @return
     */
    public String nextToken(char c) {
       
        if (currentPosition < maxPosition) {

            int start = currentPosition;
            while (currentPosition < maxPosition && c != str.charAt(currentPosition)) {
                currentPosition ++;
            }

            if (currentPosition < maxPosition) {
                return str.substring(start, currentPosition++);
            } else {
                return str.substring(start);
            }
        } else {
            return "";
        }
    }
}
