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


package ke.kalc.data.loader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DataWriteUtils {
    
    private static DateFormat tsf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
    
    /** Creates a new instance of DataWriteUtils */
    public DataWriteUtils() {
    }
    
    /**
     *
     * @param obj
     * @return
     */
    public static String getSQLValue(Object obj) {
        if (obj == null) {
            return "NULL";
        } else if (obj instanceof Double) {
            return getSQLValue((Double) obj);
        } else if (obj instanceof Integer) {
            return getSQLValue((Integer) obj);
        } else if (obj instanceof Boolean) {
            return getSQLValue((Boolean) obj);
        } else if (obj instanceof String) {
            return getSQLValue((String) obj);
        } else if (obj instanceof Date) {
            return getSQLValue((Date) obj);
        } else {
            return getSQLValue(obj.toString());
        }            
    }
    
    /**
     *
     * @param iValue
     * @return
     */
    public static String getSQLValue(Integer iValue) {
        if (iValue == null) {
            return "NULL";
        } else {
            return iValue.toString();
        }
    }
    
    /**
     *
     * @param dValue
     * @return
     */
    public static String getSQLValue(Double dValue) {
        if (dValue == null) {
            return "NULL";
        } else {
            return dValue.toString();
        }
    }
    
    /**
     *
     * @param bValue
     * @return
     */
    public static String getSQLValue(Boolean bValue) {
        if (bValue == null) {
            return "NULL";
        } else {
            return bValue.booleanValue() ? "TRUE" : "FALSE";
        }
    }
    
    /**
     *
     * @param sValue
     * @return
     */
    public static String getSQLValue(String sValue) {
        if (sValue == null) {
            return "NULL";
        } else {
            return '\'' + getEscaped(sValue) + '\'';
        }
    }
    
    /**
     *
     * @param dValue
     * @return
     */
    public static String getSQLValue(Date dValue) {
        if (dValue == null) {
            return "NULL";
        } else {
            return "{ts '" + tsf.format(dValue) + "'}";
        }
    }
    
    /**
     *
     * @param sValue
     * @return
     */
    public static String getEscaped(String sValue) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sValue.length(); i++) {
            switch (sValue.charAt(i)) {
                case '\\':
                    sb.append("\\\\");
                    break;
                 case '\'':
                    sb.append("\\'");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                default: 
                    sb.append(sValue.charAt(i));
                    break;
            }
        }
        return sb.toString();
    }
 }
