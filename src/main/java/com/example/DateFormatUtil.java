package com.example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * DateFormatUtil provides thread-safe date formatting and parsing utilities
 * for the KALCPOS system. Uses ThreadLocal to ensure thread safety without
 * synchronization overhead.
 */
public class DateFormatUtil {

    // ThreadLocal instance for date formatting (UTC)
    private static final ThreadLocal<SimpleDateFormat> dateFormat = ThreadLocal.withInitial(() -> {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UTC);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        sdf.setLenient(false); // Strict parsing
        return sdf;
    });
    
    // ThreadLocal instance for date-only formatting
    private static final ThreadLocal<SimpleDateFormat> dateOnlyFormat = ThreadLocal.withInitial(() -> {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UTC);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        sdf.setLenient(false);
        return sdf;
    });
    
    // ThreadLocal instance for time-only formatting
    private static final ThreadLocal<SimpleDateFormat> timeOnlyFormat = ThreadLocal.withInitial(() -> {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.UTC);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        sdf.setLenient(false);
        return sdf;
    });
    
    // ThreadLocal instance for ISO 8601 formatting
    private static final ThreadLocal<SimpleDateFormat> isoFormat = ThreadLocal.withInitial(() -> {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.UTC);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        sdf.setLenient(false);
        return sdf;
    });
    
    // ThreadLocal instance for file naming format
    private static final ThreadLocal<SimpleDateFormat> fileFormat = ThreadLocal.withInitial(() -> {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UTC);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        sdf.setLenient(false);
        return sdf;
    });
    
    // Custom format cache for performance
    private static final ConcurrentMap<String, ThreadLocal<SimpleDateFormat>> customFormats = 
        new ConcurrentHashMap<>();
    
    // Private constructor to prevent instantiation
    private DateFormatUtil() {
        throw new UnsupportedOperationException("DateFormatUtil is a utility class and cannot be instantiated");
    }
    
    /**
     * Gets the current date and time in UTC formatted as yyyy-MM-dd HH:mm:ss
     * @return formatted current date and time
     */
    public static String getCurrentDateTime() {
        return dateFormat.get().format(new Date());
    }
    
    /**
     * Gets the current date in UTC formatted as yyyy-MM-dd
     * @return formatted current date
     */
    public static String getCurrentDate() {
        return dateOnlyFormat.get().format(new Date());
    }
    
    /**
     * Gets the current time in UTC formatted as HH:mm:ss
     * @return formatted current time
     */
    public static String getCurrentTime() {
        return timeOnlyFormat.get().format(new Date());
    }
    
    /**
     * Gets the current date and time in ISO 8601 format (UTC)
     * @return formatted date time in ISO 8601 format
     */
    public static String getCurrentDateTimeISO() {
        return isoFormat.get().format(new Date());
    }
    
    /**
     * Gets the current date and time formatted for file names
     * @return formatted date time for file naming
     */
    public static String getCurrentDateTimeForFile() {
        return fileFormat.get().format(new Date());
    }
    
    /**
     * Format a given Date object using the default format (yyyy-MM-dd HH:mm:ss)
     * @param date the Date object to format
     * @return formatted date string
     * @throws IllegalArgumentException if date is null
     */
    public static String format(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return dateFormat.get().format(date);
    }
    
    /**
     * Format a given Date object using the date-only format (yyyy-MM-dd)
     * @param date the Date object to format
     * @return formatted date string
     * @throws IllegalArgumentException if date is null
     */
    public static String formatDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return dateOnlyFormat.get().format(date);
    }
    
    /**
     * Format a given Date object using the time-only format (HH:mm:ss)
     * @param date the Date object to format
     * @return formatted time string
     * @throws IllegalArgumentException if date is null
     */
    public static String formatTime(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return timeOnlyFormat.get().format(date);
    }
    
    /**
     * Format a given Date object using ISO 8601 format
     * @param date the Date object to format
     * @return formatted date string in ISO 8601 format
     * @throws IllegalArgumentException if date is null
     */
    public static String formatISO(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return isoFormat.get().format(date);
    }
    
    /**
     * Format a given Date object using a custom format
     * @param date the Date object to format
     * @param pattern the custom format pattern
     * @return formatted date string
     * @throws IllegalArgumentException if date or pattern is null
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (pattern == null || pattern.trim().isEmpty()) {
            throw new IllegalArgumentException("Pattern cannot be null or empty");
        }
        return getCustomFormat(pattern).format(date);
    }
    
    /**
     * Parse a date string using the default format (yyyy-MM-dd HH:mm:ss)
     * @param dateString the date string to parse
     * @return parsed Date object
     * @throws ParseException if the string cannot be parsed
     * @throws IllegalArgumentException if dateString is null or empty
     */
    public static Date parse(String dateString) throws ParseException {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be null or empty");
        }
        return dateFormat.get().parse(dateString);
    }
    
    /**
     * Parse a date string using the date-only format (yyyy-MM-dd)
     * @param dateString the date string to parse
     * @return parsed Date object
     * @throws ParseException if the string cannot be parsed
     * @throws IllegalArgumentException if dateString is null or empty
     */
    public static Date parseDate(String dateString) throws ParseException {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be null or empty");
        }
        return dateOnlyFormat.get().parse(dateString);
    }
    
    /**
     * Parse a date string using ISO 8601 format
     * @param dateString the date string to parse
     * @return parsed Date object
     * @throws ParseException if the string cannot be parsed
     * @throws IllegalArgumentException if dateString is null or empty
     */
    public static Date parseISO(String dateString) throws ParseException {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be null or empty");
        }
        return isoFormat.get().parse(dateString);
    }
    
    /**
     * Parse a date string using a custom format
     * @param dateString the date string to parse
     * @param pattern the custom format pattern
     * @return parsed Date object
     * @throws ParseException if the string cannot be parsed
     * @throws IllegalArgumentException if dateString or pattern is null or empty
     */
    public static Date parse(String dateString, String pattern) throws ParseException {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be null or empty");
        }
        if (pattern == null || pattern.trim().isEmpty()) {
            throw new IllegalArgumentException("Pattern cannot be null or empty");
        }
        return getCustomFormat(pattern).parse(dateString);
    }
    
    /**
     * Get or create a ThreadLocal SimpleDateFormat for a custom pattern
     * Uses ConcurrentHashMap for thread-safe caching
     * @param pattern the format pattern
     * @return ThreadLocal SimpleDateFormat for the pattern
     */
    private static ThreadLocal<SimpleDateFormat> getCustomFormat(String pattern) {
        return customFormats.computeIfAbsent(pattern, p -> ThreadLocal.withInitial(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat(p, Locale.UTC);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            sdf.setLenient(false);
            return sdf;
        }));
    }
    
    /**
     * Clear all ThreadLocal instances to prevent memory leaks
     * Should be called when threads are about to terminate (e.g., in web applications)
     */
    public static void clearThreadLocals() {
        dateFormat.remove();
        dateOnlyFormat.remove();
        timeOnlyFormat.remove();
        isoFormat.remove();
        fileFormat.remove();
        customFormats.values().forEach(ThreadLocal::remove);
    }
    
    /**
     * Clear a specific custom format from the cache
     * @param pattern the format pattern to remove
     */
    public static void clearCustomFormat(String pattern) {
        ThreadLocal<SimpleDateFormat> format = customFormats.remove(pattern);
        if (format != null) {
            format.remove();
        }
    }
    
    /**
     * Clear all custom formats from the cache
     */
    public static void clearAllCustomFormats() {
        customFormats.values().forEach(ThreadLocal::remove);
        customFormats.clear();
    }
    
    /**
     * Get the number of cached custom formats
     * @return count of cached custom formats
     */
    public static int getCustomFormatCount() {
        return customFormats.size();
    }
    
    /**
     * Validate if a date string matches the default format
     * @param dateString the date string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidFormat(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return false;
        }
        try {
            dateFormat.get().parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    
    /**
     * Validate if a date string matches a specific format pattern
     * @param dateString the date string to validate
     * @param pattern the format pattern
     * @return true if valid, false otherwise
     */
    public static boolean isValidFormat(String dateString, String pattern) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return false;
        }
        try {
            getCustomFormat(pattern).get().parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    
    /**
     * Calculate the time difference between two dates in milliseconds
     * @param date1 first date
     * @param date2 second date
     * @return difference in milliseconds
     * @throws IllegalArgumentException if any date is null
     */
    public static long differenceInMillis(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        return Math.abs(date1.getTime() - date2.getTime());
    }
    
    /**
     * Get current timestamp in milliseconds
     * @return current time in milliseconds since epoch
     */
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }
    
    /**
     * Convert milliseconds to a formatted date string
     * @param millis milliseconds since epoch
     * @return formatted date string
     */
    public static String fromMillis(long millis) {
        return dateFormat.get().format(new Date(millis));
    }
}