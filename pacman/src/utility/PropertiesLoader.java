package src.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    // properties location entry extension (for representing an object's location in properties file)
    public static final String LOCATION_EXTENSION = ".location";
    // properties move entry extension (for representing PacMan's move sequence in properties file)
    public static final String MOVE_EXTENSION = ".move";
    // properties entry extension (for representing an object's location in properties file)
    public static final String AUTO_EXTENSION = ".isAuto";

    // PacMan auto-mode movement properties
    public static final String RIGHT_DIR = "R";
    public static final String LEFT_DIR = "L";
    public static final String MOVE_DIR = "M";


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
