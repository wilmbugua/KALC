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

import uk.kalc.format.Formats;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtil {
    
    
      public static int getTotalMinute(Date d) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        int hour =calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int hourToMinute = (hour * 60) + minute ;
       return hourToMinute;
    }  
     
      
     public static String getAppDateStartHour(Date d) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.set(Calendar.HOUR,calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,1);
        calendar.set( Calendar.AM_PM, Calendar.AM);
        Date dddd = calendar.getTime();  
        return Formats.TIMESTAMP.formatValue(dddd);  
    }  
     
     
      public static String getAppDateStartHour(Date d , int amount ) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.add(Calendar.DATE, amount);
        calendar.set(Calendar.HOUR,calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,1);
        calendar.set( Calendar.AM_PM, Calendar.AM);
        Date dddd = calendar.getTime();    
        return Formats.TIMESTAMP.formatValue(dddd);
    }  
     
     
     public static String getAppDateLastHour(Date d) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set( Calendar.AM_PM, Calendar.PM);
        Date dddd = calendar.getTime();  
       return Formats.TIMESTAMP.formatValue(dddd);
    }  
     
     
     
     public static String getAppDateLastHour(Date d, int amount) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.add(Calendar.DATE, amount);
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set( Calendar.AM_PM, Calendar.PM);
        Date dddd = calendar.getTime();  
       return Formats.TIMESTAMP.formatValue(dddd);
    }  
     
  
     
     
     public static String getFirstMonthDay(Date d) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.set(Calendar.DAY_OF_MONTH, 0);  
        calendar.set(Calendar.HOUR,calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,1);
        calendar.set( Calendar.AM_PM, Calendar.AM);
        Date dddd = calendar.getTime();  
        return Formats.TIMESTAMP.formatValue(dddd); 
    }  
     
     public static String getFirstMonthDay(Date d , int amount) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.add(Calendar.MONTH, amount);
        calendar.set(Calendar.DAY_OF_MONTH, 0);  
        calendar.set(Calendar.HOUR,calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,1);
        calendar.set( Calendar.AM_PM, Calendar.AM);
        Date dddd = calendar.getTime();  
        return Formats.TIMESTAMP.formatValue(dddd);
    }  
     
     
       public static String getLastMonthDay(Date d) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); 
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set( Calendar.AM_PM, Calendar.PM);
        Date dddd = calendar.getTime();  
        return Formats.TIMESTAMP.formatValue(dddd); 
    }  
       
       
     public static String getLastMonthDay(Date d  , int amount) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.add(Calendar.MONTH, amount);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); 
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set( Calendar.AM_PM, Calendar.PM);
        Date dddd = calendar.getTime();  
       return Formats.TIMESTAMP.formatValue(dddd);
    }  
       
     
     
        public static String getFirstYearDay(Date d) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.set(Calendar.DAY_OF_YEAR, 1);  
        calendar.set(Calendar.HOUR,calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,1);
        calendar.set( Calendar.AM_PM, Calendar.AM);
        Date dddd = calendar.getTime();  
       return Formats.TIMESTAMP.formatValue(dddd);
    }  
        
        public static String getFirstYearDay(Date d , int amount) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.add(Calendar.YEAR, amount);
        calendar.set(Calendar.DAY_OF_YEAR, 1);  
        calendar.set(Calendar.HOUR,calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,1);
        calendar.set( Calendar.AM_PM, Calendar.AM);
        Date dddd = calendar.getTime();  
        return Formats.TIMESTAMP.formatValue(dddd); 
    }  
     
     
    
  
    
    
    public static String getLastYearDay(Date d) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR)); 
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set( Calendar.AM_PM, Calendar.PM);
        Date dddd = calendar.getTime();  
        return Formats.TIMESTAMP.formatValue(dddd);
    }  
    
    
    public static String getLastYearDay(Date d , int amount) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.add(Calendar.YEAR, amount);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR)); 
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set( Calendar.AM_PM, Calendar.PM);
        Date dddd = calendar.getTime();  
       return Formats.TIMESTAMP.formatValue(dddd);  
    }  
    
    
    public static int getHour(Date d){
         SimpleDateFormat sdf1 = new SimpleDateFormat("HH");  
//        "Feb 1, 2013 12:00:00 AM"
        return Integer.parseInt(sdf1.format(d));  
    }
    
    
    public static String getDayOfTheWeek(Date d){
      SimpleDateFormat sdf1=new SimpleDateFormat("EEEE"); 
       return  sdf1.format(d);
    }
    
     public static String getDayOfTheMonth(Date d){
      SimpleDateFormat sdf1=new SimpleDateFormat("dd"); 
       String dateResult=  sdf1.format(d);
       return dateResult;
    }
     
     public static String getLastDayOfTheMonth(Date d){
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); 
//        calendar.set( Calendar.AM_PM, Calendar.PM);
        Date dddd = calendar.getTime();  
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd");  
//        "Feb 1, 2013 12:00:00 AM"
        return sdf1.format(dddd);  
    }
    
    
       
        public static Date getDateFirstHour(Date d) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.set(Calendar.HOUR,calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set( Calendar.AM_PM, Calendar.AM);
        Date dddd = calendar.getTime();  
        return dddd;  
    }
        
        
     public static Date getDateLastHour(Date d) throws Exception  
    {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.set(Calendar.HOUR,11);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        calendar.set( Calendar.AM_PM, Calendar.PM);
        Date dddd = calendar.getTime();  
        return dddd;  
    }
        
}
