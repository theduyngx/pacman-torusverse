package pacman;

import ch.aplu.jgamegrid.Location;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;


public class LevelChecker {
    private final PathFinding pathFinding;
    private LinkedList<Location> path;

    public LevelChecker() {
        this.pathFinding = new PathFinding();
    }

    public LinkedList<Location> getPath() {
        return path;
    }

    public HashMap<HashLocation, Item> unreachableItems(Game game) {
        PacActor pacActor = game.getManager().getPacActor();
        HashMap<HashLocation, Item> items = game.getManager().getMandatoryItems();
        path = pathFinding.idsFull(pacActor);
        HashMap<HashLocation, Item> reachable = new HashMap<>();

        // get the reachable hash map
        for (Location loc : path) {
            if (HashLocation.contain(items, loc))
                HashLocation.put(reachable, loc, HashLocation.get(items, loc));
        }

        // get unreachable hash map
        HashMap<HashLocation, Item> unreachable = new HashMap<>();
        for (HashLocation hashLoc : items.keySet()) {
            if (! reachable.containsKey(hashLoc))
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
