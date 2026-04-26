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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for loading properties from kalcconfig.properties
 */
public class PropertyUtil {

    private static Properties props = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream input = new FileInputStream("kalcconfig.properties")) {
            props.load(input);
        } catch (IOException ex) {
            // If file not found or error loading, continue with empty properties
            // In a real application, you might want to log this
        }
    }

    /**
     * Get property value by key
     * @param key property key
     * @return property value or null if not found
     */
    public static String getProperty(String key) {
        return props.getProperty(key);
    }

    /**
     * Get property value by key with default value
     * @param key property key
     * @param defaultValue default value if property not found
     * @return property value or default value
     */
    public static String getProperty(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
}