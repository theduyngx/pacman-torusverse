package pacman;


import ch.aplu.jgamegrid.Location;

import java.util.HashMap;
import java.util.LinkedList;

public class LevelChecking {
    private final PathFinding pathFinding;

    public LevelChecking() {
        this.pathFinding = new PathFinding();
    }

    public boolean unreachableItems(PacActor pacActor, HashMap<HashableLocation, Item> items) {
        LinkedList<Location> path = pathFinding.bfs(pacActor);
        return false;
    }
}
