package DAMS.Replicas.Replica2.server.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
    public static String getPropValue(String key) throws IOException {
        Properties prop = new Properties();
        String propFileName = "config.properties";

        InputStream is = ConfigUtil.class.getClassLoader().getResourceAsStream(propFileName);
        if (is != null) {
            prop.load(is);
        } else {
            throw new FileNotFoundException(String.format("Property file '%s' not found in the classpath", propFileName));
        }
        return prop.getProperty(key);
    }
}
