import editor.Controller;
import game.Game;
import game.utility.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;


/**
 * Main entry to program. It deals with instantiating the Game and the Controller. In instantiating
 * Game, it will check for Game's validity. The Controller will be responsible for checking Level's
 * validity of Game.
 * @see    Controller
 * @see	   Game
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
		/// NOTE:
		// argument parsing


		// get all playable levels
		ArrayList<String> playableLevels = null;
		GameCallback gameCallback = new GameCallback();
		String path = "test";
		File file = new File(path);
		if (file.isDirectory())
			playableLevels = gameCheck(file, gameCallback);

		// instantiate the Game and let the Controller handle the program
		/// NOTE: REMOVE PROPERTIES_PATH AFTER!!! - TESTING WON'T ALLOW THIS
		if (playableLevels != null) {
			String propertiesPath = PropertiesLoader.PROPERTIES_PATH + PROPERTIES_FILE;
			Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
			Game game = new Game(properties, gameCallback);
			new Controller(game, playableLevels, gameCallback);
		}
	}

	/**
	 * Check the validity of a game folder
	 * @param directory the path of the directory
	 * @param callback  the game callback
	 * @return 		    whether the gameCheck fail or succeed
	 */
	public static ArrayList<String> gameCheck(File directory, GameCallback callback) {
		String[] dirNameSplit = directory.getName().split("(/)|(\\\\)");
		String dirName = dirNameSplit[dirNameSplit.length - 1];
		File[] gameMaps = directory.listFiles();

		// no game maps found
		if (gameMaps == null) {
			String failLog = String.format("[Game %s - no maps found]", dirName);
			callback.writeString(failLog);
			return null;
		}
		HashMap<Integer, ArrayList<String>> levelTally = new HashMap<>();

		// build a hashmap with the key as levels and filename as value
		for (File map: gameMaps) {
			String[] mapNameSplit = map.getName().split("(/)|(\\\\)");
			String mapName = mapNameSplit[mapNameSplit.length - 1];
			char firstChar = mapName.charAt(0);

			/// NOTE: CHECK FOR .TXT FILES
			// add filename to hashmap given that it is valid
			if (Character.isDigit(firstChar)) {
				Integer decimalRep = Integer.parseInt(String.valueOf(firstChar));

				// add file to arraylist
				if (levelTally.containsKey(decimalRep))
					levelTally.get(decimalRep).add(map.getName());
				else {
					ArrayList<String> files = new ArrayList<>();
					files.add(map.getPath());
					levelTally.put(decimalRep, files);
				}
			}
		}
		// sort the levels lexicographically
		if (gameCheckLog(levelTally, dirName, callback)) {
			ArrayList<String> playableLevels = new ArrayList<>();
			for (ArrayList<String> levels : levelTally.values())
				/// NOTE: keep the full file name + path
				playableLevels.add(levels.get(0));
			Collections.sort(playableLevels);

			///
			for (String level : playableLevels)
				System.out.println(level);
			///

			return playableLevels;
		}
		return null;
	}

	/**
	 * write the check fails for game checking to the log
	 * @param levelTally hashmap of files at a particular level
	 * @param dirName    the name of the directory
	 * @return           whether the directory has fail any game check
	 */
	private static boolean gameCheckLog(HashMap<Integer, ArrayList<String>> levelTally, String dirName,
										GameCallback callback)
	{
		boolean pass = true;

		// check the hashmap for check failure and print the corresponding issues
		if (levelTally.isEmpty()) {
			String failLog = String.format("[Game %s - no maps found]", dirName);
			callback.writeString(failLog);
			pass = false;
		}
		else {
			// loop through hashmap, check that the array list is greater than 1.
			for (ArrayList<String> level: levelTally.values()) {
				if (level.size() > 1) {
					String dupFiles = String.join("; ", level);
					String failLog = String.format("[Game %s - multiple maps at same level: %s]", dirName, dupFiles);
					callback.writeString(failLog);
					pass = false;
				}
			}
		}
		return pass;
	}
}
