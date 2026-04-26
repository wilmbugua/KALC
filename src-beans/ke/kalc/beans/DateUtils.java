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


package ke.kalc.beans;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 *   
 */
public class DateUtils {
    
    /** Creates a new instance of DateUtils */
    private DateUtils() {
    }
    
    /**
     *
     * @return
     */
    public static Date getToday() { 
        // el dia de hoy sin horas ni nada.        
        return getToday(new Date());     
    }
    
    /**
     *
     * @param d
     * @return
     */
    public static Date getToday(Date d) { 
        // el dia de hoy sin horas ni nada.        
        GregorianCalendar ddate = new GregorianCalendar();
        ddate.setTime(d);    
        GregorianCalendar ddateday = new GregorianCalendar(ddate.get(GregorianCalendar.YEAR), ddate.get(GregorianCalendar.MONTH), ddate.get(GregorianCalendar.DAY_OF_MONTH));
        return ddateday.getTime();        
    }
    
    /**
     *
     * @return
     */
    public static Date getTodayMinutes() { 
        // el dia de hoy sin horas ni nada.        
        return getTodayMinutes(new Date());     
    }
    
    /**
     *
     * @param d
     * @return
     */
    public static Date getTodayMinutes(Date d) { 
        // el dia de hoy sin horas ni nada.        
        GregorianCalendar ddate = new GregorianCalendar();
        ddate.setTime(d);    
        GregorianCalendar ddateday = new GregorianCalendar(ddate.get(GregorianCalendar.YEAR), ddate.get(GregorianCalendar.MONTH), ddate.get(GregorianCalendar.DAY_OF_MONTH)
                                                         , ddate.get(GregorianCalendar.HOUR_OF_DAY), ddate.get(GregorianCalendar.MINUTE));
        return ddateday.getTime();        
    }
    
    /**
     *
     * @param d
     * @return
     */
    public static Date getTodayHours(Date d) {
        // el dia ajustado a las horeas     
        Calendar ddate = Calendar.getInstance();
        ddate.setTime(d);    
        
        Calendar dNow = Calendar.getInstance();
        dNow.clear();
        dNow.set(ddate.get(Calendar.YEAR), ddate.get(Calendar.MONTH), ddate.get(Calendar.DAY_OF_MONTH)
               , ddate.get(Calendar.HOUR_OF_DAY), 0, 0);
        
        return dNow.getTime();        
    }
    
    /**
     *
     * @param day
     * @param hour
     * @return
     */
    public static Date getDate(Date day, Date hour) {
        
        // Calculamos el dia actual con la hora escogida.
        Calendar dDay = Calendar.getInstance();
        dDay.setTime(day);
        Calendar dHour = Calendar.getInstance();
        dHour.setTime(hour);    
        
        Calendar dNow = Calendar.getInstance();
        dNow.clear();
        dNow.set(dDay.get(Calendar.YEAR), dDay.get(Calendar.MONTH), dDay.get(Calendar.DAY_OF_MONTH)
        , dHour.get(Calendar.HOUR_OF_DAY), dHour.get(Calendar.MINUTE), dHour.get(Calendar.SECOND));
        
        return dNow.getTime();
    }

}
