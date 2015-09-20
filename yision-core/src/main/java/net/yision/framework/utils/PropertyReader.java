package net.yision.framework.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * Created by Jeffrey on 15/9/21.
 */
public class PropertyReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyReader.class);
    private static final Properties PROPS = new Properties();

    static {
        InputStream stream = PropertyReader.class.getResourceAsStream("/config/application.properties");
        try {
            PROPS.load(stream);
            stream.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private PropertyReader() {

    }

    public static Properties getInstance() {
        return PROPS;
    }

    public static String getProperty(String key) {
        return PROPS.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return PROPS.getProperty(key, defaultValue);
    }
}
