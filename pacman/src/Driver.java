import editor.Controller;
import game.Game;
import game.utility.*;

import java.util.ArrayList;
import java.util.Properties;


/**
 * Main entry to program. It deals with instantiating the Game and the Controller. In instantiating
 * Game, it will check for Game's validity. The Controller will be responsible for checking Level's
 * validity of Game.
 * @see Controller
 * @see	Game
 *
 * @author The Duy Nguyen            - 1100548 (theduyn@student.unimelb.edu.au)
 * @author Ramon Javier L. Felipe VI - 1233281 (rfelipe@student.unimelb.edu.au)
 * @author Jonathan Chen Jie Kong    - 1263651 (jonathanchen@student.unimelb.edu.au)
 */
public class Driver {
	// test properties file
	public static final String PROPERTIES_FILE = "test.properties";

	/**
	 * Main entry to program.
	 */
	public static void main(String[] args) {
		// argument parsing
		String path = (args.length == 0) ? "" : args[0];

		// get all playable levels
		GameCallback gameCallback = new GameCallback();
		GameChecker gameChecker = new GameChecker();
		ArrayList<String> playableLevels = gameChecker.gameCheck(path, gameCallback);

		// instantiate the Game and let the Controller handle the program
		if (playableLevels != null) {
			Properties properties = PropertiesLoader.loadPropertiesFile(PROPERTIES_FILE);
			Game game = new Game(properties, gameCallback);
			new Controller(game, gameChecker.getGameType(), playableLevels, gameCallback);
		}
	}
}
