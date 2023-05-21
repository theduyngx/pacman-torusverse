package src;
import src.utility.PropertiesLoader;
import java.util.Properties;


/**
 * Based on skeleton code for SWEN20003 Project, Semester 2, 2022, The University of Melbourne.
 * The Driver class which is the main entry to the game.
 *
 * @author The Duy Nguyen            - 1100548 (theduyn@student.unimelb.edu.au)
 * @author Ramon Javier L. Felipe VI - 1233281 (rfelipe@student.unimelb.edu.au)
 * @author Jonathan Chen Jie Kong    - 1263651 (jonathanchen@student.unimelb.edu.au)
 */
public class Driver {
    public static final String DEFAULT_PROPERTIES_PATH = "pacman/properties/test5.properties";

    /**
     * Entry point to program.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String propertiesPath = DEFAULT_PROPERTIES_PATH;
        if (args.length > 0) {
            propertiesPath = args[0];
        }
        final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
        Game game = new Game(properties);
        game.run();
    }
}
