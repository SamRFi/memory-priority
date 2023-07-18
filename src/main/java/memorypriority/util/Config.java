package memorypriority.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config {
    public static final String CONFIG_FILE = "/config/config.properties";
    public static final Config INSTANCE = new Config();
    private final Properties properties = new Properties();
    public static final Logger LOGGER = Logger.getLogger(Config.class.getName());

    private Config() {
        try (InputStream ris = getClass().getResourceAsStream(CONFIG_FILE)) {
            properties.load(ris);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "unable to read config file");
            throw new MemoryPriorityException("unable to read config file");
        }
    }

    public static Config getInstance() {
        return INSTANCE;
    }

    public String readSetting(String key) {
        return properties.getProperty(key);
    }

    public String readSetting(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public void writeSetting(String key, String value) {
        properties.setProperty(key, value);
        storeSettingsToFile();
    }

    private void storeSettingsToFile() {
        String path = getClass().getResource(CONFIG_FILE).getPath();

        try (FileOutputStream fos = new FileOutputStream(path)) {
            properties.store(fos, null);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "could not find file");
            throw new MemoryPriorityException("can't store to file");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "could not read from file" );
            throw new MemoryPriorityException("can't store to file");
        }
    }
}