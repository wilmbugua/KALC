package ke.kalc.pos.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author
 */
public class PropertyUtils {
    private static final Logger logger = Logger.getLogger(PropertyUtils.class.getName());
    private static final String CONFIG_PATH = System.getenv("KALC_CONFIG_PATH") != null ? System.getenv("KALC_CONFIG_PATH") : System.getProperty("kalc.config.path", Paths.get(System.getProperty("user.home")).toString() + "/default-config.properties");

    public static Properties loadProperties() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(CONFIG_PATH)) {
            properties.load(input);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load properties from configuration file: " + CONFIG_PATH, e);
        }
        return properties;
    }
}
