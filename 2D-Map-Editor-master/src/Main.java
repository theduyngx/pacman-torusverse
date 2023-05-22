import editor.Controller;
import pacman.Game;
import pacman.utility.PropertiesLoader;
import java.util.Properties;


public class Main {
	public static void main(String[] args) {
		String propertiesPath = PropertiesLoader.PROPERTIES_PATH + "test1.properties";
		Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
		assert properties != null;
		Game game = new Game(properties);
		new Controller(game);
	}
}
