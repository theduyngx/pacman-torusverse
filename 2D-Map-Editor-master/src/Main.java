import editor.Controller;
import pacman.src.Game;
import pacman.src.utility.PropertiesLoader;
import java.util.Properties;


public class Main {
	public static void main(String[] args) {
		String propertiesPath = "src/pacman/properties/test1.properties";
		Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
		assert properties != null;
		Game game = new Game(properties);
		new Controller(game);
	}
}
