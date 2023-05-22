package pacman;

import ch.aplu.jgamegrid.Location;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class LevelChecker {
    private final PathFinding pathFinding;
    private LinkedList<Location> path;

    public LevelChecker() {
        this.pathFinding = new PathFinding();
    }

    public LinkedList<Location> getPath() {
        return path;
    }

    public HashMap<HashableLocation, Item> unreachableItems(Game game) {
        PacActor pacActor = game.getManager().getPacActor();
        HashMap<HashableLocation, Item> items = game.getManager().getMandatoryItems();
        path = pathFinding.bfs(pacActor);
        HashMap<HashableLocation, Item> unreachable = new HashMap<>();

        // fast check
        if (path.size() == items.size())
            return unreachable;

        // get the unreachable hash map
        for (Location loc : path) {
            HashableLocation key = new HashableLocation(loc);
            if (! items.containsKey(key))
                unreachable.put(key, items.get(key));
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
