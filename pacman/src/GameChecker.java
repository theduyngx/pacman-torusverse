import editor.Controller.GameType;
import game.utility.GameCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


/**
 * GameChecker class checking for the game's validity with various conditions.
 */
public class GameChecker {
    public static final String VALID_MAP_FILE = "xml";
    private GameType gameType;


    /**
     * Get the type of game open - either a folder, a file, or nothing.
     * @return the game type
     */
    public GameType getGameType() {
        return gameType;
    }

    /**
     * Call to game callback to report no maps found.
     * @param directory the directory string
     * @param callback  the game callback
     */
    private void callbackNoMap(String directory, GameCallback callback) {
        String failLog = String.format("[Game %s - no maps found]", directory);
        callback.writeString(failLog);
        gameType = GameType.IS_NULL;
    }

    /**
     * Check the validity of a game folder
     * @param path      the path of the directory
     * @param callback  the game callback
     * @return 		    whether the gameCheck fail or succeed
     */
    public ArrayList<String> gameCheck(String path, GameCallback callback) {
        if (path.isEmpty()) {
            callbackNoMap(path, callback);
            return null;
        }
        File directory = new File(path);

        // check type
        if (directory.isDirectory())
            gameType = GameType.IS_FOLDER;
        else if (directory.isFile())
            gameType = GameType.IS_FILE;
        else {
            callbackNoMap(path, callback);
            return null;
        }

        String[] dirNameSplit = directory.getName().split("(/)|(\\\\)");
        String dirName = dirNameSplit[dirNameSplit.length - 1];
        File[] gameMaps = directory.listFiles();

        // no game maps found
        if (gameMaps == null) {
            callbackNoMap(dirName, callback);
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
            for (ArrayList<String> levels : levelTally.values()) {
                String name = levels.get(0);
                int index = name.lastIndexOf(".");
                // skip non-XML files
                String extension = name.substring(index + 1).toLowerCase();
                if (extension.equals(VALID_MAP_FILE))
                    playableLevels.add(name);
            }
            Collections.sort(playableLevels);
            return playableLevels;
        }
        callbackNoMap(dirName, callback);
        return null;
    }


    /**
     * write the check fails for game checking to the log
     * @param levelTally hashmap of files at a particular level
     * @param dirName    the name of the directory
     * @return           whether the directory has fail any game check
     */
    private boolean gameCheckLog(HashMap<Integer, ArrayList<String>> levelTally, String dirName,
                                        GameCallback callback)
    {
        boolean pass = true;

        // check the hashmap for check failure and print the corresponding issues
        if (levelTally.isEmpty()) {
            callbackNoMap(dirName, callback);
            pass = false;
        }
        else {
            // loop through hashmap, check that the array list is greater than 1.
            for (ArrayList<String> level: levelTally.values()) {
                if (level.size() > 1) {
                    String dupFiles = String.join("; ", level);
                    String failLog = String.format(
                            "[Game %s - multiple maps at same level: %s]", dirName, dupFiles);
                    callback.writeString(failLog);
                    pass = false;
                    gameType = GameType.IS_NULL;
                }
            }
        }
        return pass;
    }
}
