import editor.Controller;
import pacman.Game;
import pacman.HashLocation;
import pacman.Item;
import pacman.LevelChecker;
import pacman.utility.GameCallback;
import pacman.utility.PropertiesLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;


public class Main {
	public static void main(String[] args) {

		boolean checkPass;
		GameCallback gameCallback = new GameCallback();
		String path = "test";
		File filePath = new File(path);
		if (filePath.isDirectory())
			checkPass = gameCheck(path, gameCallback);

		checkPass = true;
		if (checkPass) {
			String propertiesPath = PropertiesLoader.PROPERTIES_PATH + "test6.properties";
			Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
			assert properties != null;
			Game game = new Game(properties, gameCallback);

			/// NOTE: this part of level checking should be within the level checking itself
			/// then the unreachable must be printed and moved to object manager for log update accordingly
			LevelChecker levelChecker = new LevelChecker();
			HashMap<HashLocation, Item> unreachable = levelChecker.unreachableItems(game);
			boolean setStart = (unreachable.size() == 0);
			new Controller(game, setStart);
		}
	}

	/**
	 * Check the validity of a game folder
	 * @param path 	   the path of the directory
	 * @param callback the game callback
	 * @return 		   whether the gameCheck fail or succeed
	 */
	public static boolean gameCheck(String path, GameCallback callback) {
		File directory = new File(path);
		String[] dirNameSplit = directory.getName().split("/");
		String dirName = dirNameSplit[dirNameSplit.length - 1];

		File[] gameMaps = directory.listFiles();
		if (gameMaps == null)
			return false;
		HashMap<Integer, ArrayList<String>> levelTally = new HashMap<>();

		// build a hashmap with the key as levels and filename as value
		for (File map: gameMaps) {
			String[] mapNameSplit = map.getName().split("/");
			String mapName = mapNameSplit[mapNameSplit.length - 1];
			char firstChar = mapName.charAt(0);

			// add filename to hashmap given that it is valid
			if (Character.isDigit(firstChar)) {
				Integer decimalRep = Integer.parseInt(String.valueOf(firstChar));

				// add file to arraylist
				if (levelTally.containsKey(decimalRep)) {
					levelTally.get(decimalRep).add(map.getName());
				} else {
					ArrayList<String> files = new ArrayList<>();
					files.add(map.getName());
					levelTally.put(decimalRep, files);
				}
			}
		}
		return gameCheckLog(levelTally, dirName, callback);
	}

	/**
	 * write the check fails for game checking to the log
	 * @param levelTally hashmap of files at a particular level
	 * @param dirName    the name of the directory
	 * @return           whether the directory has fail any game check
	 */
	private static boolean gameCheckLog(HashMap<Integer, ArrayList<String>> levelTally, String dirName,
										GameCallback callback) {
		boolean pass = true;

		// check the hashmap for check failure and print the corresponding issues
		if (levelTally.isEmpty()) {
			String failLog = String.format("[Game %s - no maps found]", dirName);
			callback.writeString(failLog);
			pass = false;
		} else {
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
