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


package ke.kalc.pos.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertyUtils {

    private static final Logger logger = Logger.getLogger(PropertyUtils.class.getName());
    private Properties m_propsconfig;
    private File configFile;
    private final String APP_ID = "upos-app";

    /**
     *
     */
    public PropertyUtils() {
        init(getDefaultConfig());
    }

    private void init(File configfile) {
        this.configFile = configfile;
        load();
    }

    private File getDefaultConfig() {
        // Externalize config path via environment variable
        String configPath = System.getenv("KALC_CONFIG");
        if (configPath != null && !configPath.trim().isEmpty()) {
            return new File(configPath);
        }
        return new File(new File("./"), "KALC.properties");
    }

    private void load() {
        // Load Properties
        try {
            InputStream in = new FileInputStream(configFile);
            if (in != null) {
                m_propsconfig = new Properties();
                m_propsconfig.load(in);
                in.close();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load configuration file: {0}", e.getMessage());
        }
    }

    /**
     *
     * @param sKey
     * @return
     */
    public String getProperty(String sKey) {
        return m_propsconfig.getProperty(sKey);
    }

    /**
     *
     * @return
     */
    public String getDriverName() {
        return m_propsconfig.getProperty("db.driver");
    }

    /**
     *
     * @return
     */
    public String getUrl() {
        return m_propsconfig.getProperty("db.URL");
    }

    /**
     *
     * @return
     */
    public String getDBUser() {
        return m_propsconfig.getProperty("db.user");
    }

    /**
     *
     * @return
     */
    public String getDBPassword() {
        return m_propsconfig.getProperty("db.password");
    }
}
