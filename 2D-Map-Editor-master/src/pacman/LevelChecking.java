package pacman;

import ch.aplu.jgamegrid.Location;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class LevelChecking {
    private final PathFinding pathFinding;
    private LinkedList<Location> path;

    public LevelChecking() {
        this.pathFinding = new PathFinding();
    }

    public LinkedList<Location> getPath() {
        return path;
    }

    public HashMap<HashableLocation, Item> unreachableItems(PacActor pacActor,
                                                            HashMap<HashableLocation, Item> items) {
        path = pathFinding.bfs(pacActor);
        // deep copy
        HashMap<HashableLocation, Item> obtainable = new HashMap<>();
        for (Map.Entry<HashableLocation, Item> entry : items.entrySet())
            obtainable.put(new HashableLocation(entry.getKey().location()), entry.getValue());
        // get the obtainable hash map
        for (Location loc : path) {
            HashableLocation key = new HashableLocation(loc);
            obtainable.remove(key);
        }
        return obtainable;
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
