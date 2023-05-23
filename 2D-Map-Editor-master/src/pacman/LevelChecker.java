package pacman;

import ch.aplu.jgamegrid.Location;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * The level checker class to check for level validity. This will dictate whether the gameplay mode
 * or the editor mode will be initiated by the Controller.
 * @see editor.Controller
 */
public class LevelChecker {
    private final PathFinder pathFinder = new PathFinder();


    /**
     * Get the unreachable mandatory items. Mandatory items are items that must be obtained for the
     * player to win. If there are any unreachable mandatory items, the game will not be initiated
     * and instead the editor mode will be. This is all part of Controller's behaviors.
     * @param game the game
     * @return     the map of unreachable items
     */
    public HashMap<HashLocation, Item> unreachableItems(Game game) {
        // relevant objects and instantiations
        PacActor pacActor = game.getManager().getPacActor();
        HashMap<HashLocation, Item> items = game.getManager().getMandatoryItems();
        ArrayList<Location> reachable = pathFinder.dfsGreedyCheck(pacActor);
        HashMap<HashLocation, Item> reachableMap = new HashMap<>();

        // get the reachable hash map
        for (Location loc : reachable) {
            if (HashLocation.contain(items, loc))
                HashLocation.put(reachableMap, loc, HashLocation.get(items, loc));
        }

        // get unreachable hash map
        HashMap<HashLocation, Item> unreachable = new HashMap<>();
        for (HashLocation hashLoc : items.keySet()) {
            if (! reachableMap.containsKey(hashLoc))
                unreachable.put(hashLoc, items.get(hashLoc));
        }
        return unreachable;
    }

    public int getNumPacActor() {
        return 1;
    }

    public boolean checkLevel(File file) {
        // call XML parser to get the objects
        // ...
        return false;
    }
}
