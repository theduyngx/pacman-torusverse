package game.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertiesLoader {
    // properties related paths
    public static final String PATH = "sprites/";
    public static final String PROPERTIES_PATH = "properties/";
    // properties entry extension (for representing an object's location in properties file)
    public static final String AUTO_EXTENSION = ".isAuto";
    public static final String SEED = "seed";
    public static final String VERSION = "version";
    public static final String IS_MULTIVERSE = "multiverse";


    public static Properties loadPropertiesFile(String propertiesFile) {
        try (InputStream input = new FileInputStream(propertiesFile)) {
            Properties prop = new Properties();

            // load a properties file
            prop.load(input);
            if (prop.getProperty("PacMan.move").equals(""))
                prop.remove("PacMan.move");

            if (prop.getProperty("Pills.location").equals(""))
                prop.remove("Pills.location");

            if (prop.getProperty("Gold.location").equals(""))
                prop.remove("Gold.location");

            return prop;
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
