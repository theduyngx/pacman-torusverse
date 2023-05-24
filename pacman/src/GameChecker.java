import game.utility.GameCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


/**
 * GameChecker class checking for the game's validity with various conditions.
 * TODO: path (argument) handling
 */
public class GameChecker {
    /**
     * Check the validity of a game folder
     * @param path      the path of the directory
     * @param callback  the game callback
     * @return 		    whether the gameCheck fail or succeed
     */
    public static ArrayList<String> gameCheck(String path, GameCallback callback) {
        if (path.isEmpty())
            return null;
        File directory = new File(path);
        if (! directory.isDirectory())
            return null;

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
