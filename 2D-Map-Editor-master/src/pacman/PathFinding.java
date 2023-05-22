package pacman;
import static pacman.LiveActor.*;

import ch.aplu.jgamegrid.Location;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * LEVEL CHECKING:
 * 1. Check the number of pacActors (cannot exceed 1, if 0 then go to Level editor)
 * 2. Beware of the log file when running game in background
 * 3. Portal validity, each must corresponds to another and cannot have 2 portals of same color (factory method)
 * 4. Path finding to check if any pill/gold is unreachable
 */
public class PathFinding {
    public ArrayList<Location> getAllMoves(LiveActor actor) {
        ArrayList<Location> moves = new ArrayList<>();
        Location next;
        int[] turn_angles = new int[]{RIGHT_TURN_ANGLE, LEFT_TURN_ANGLE, FORWARD_TURN_ANGLE, BACK_TURN_ANGLE};
        for (int turnAngle : turn_angles) {
            actor.setDirection(turnAngle);
            actor.turn(turnAngle);
            next = actor.getNextMoveLocation();
            if (actor.canMove(next)) moves.add(next);
        }
        return moves;
    }

    private static class LocationPath {
        private final Location location;
        private LocationPath parent = null;

        private LocationPath(Location location) {
            this.location = location;
        }
        private void setParent(LocationPath parent) {
            this.parent = parent;
        }

        private static LinkedList<Location> getPath(LocationPath locationPath) {
            LinkedList<Location> path = new LinkedList<>();
            while (locationPath != null) {
                path.addFirst(locationPath.location);
                locationPath = locationPath.parent;
            }
            return path;
        }
    }

    public LinkedList<Location> bfs(PacActor pacActor) {
        ObjectManager manager = pacActor.getManager();
        // deep copy
        HashMap<HashableLocation, Item> items = new HashMap<>();
        for (Map.Entry<HashableLocation, Item> entry : manager.getItems().entrySet())
            items.put(new HashableLocation(entry.getKey().location()), entry.getValue().deepCopy());

        LinkedList<LocationPath> locationQueue = new LinkedList<>();
        locationQueue.addLast(new LocationPath(pacActor.getLocation()));

        while (! locationQueue.isEmpty()) {
            LocationPath path = locationQueue.removeFirst();
            pacActor.setLocation(path.location);
            pacActor.addVisitedMap(path.location);

            // update items hashmap
            items.remove(new HashableLocation(path.location));

            if (items.size() == 0)
                return LocationPath.getPath(path);
            ArrayList<Location> nextLocations = getAllMoves(pacActor);
            for (Location next : nextLocations) {
                if (! pacActor.hasVisited(next)) {
                    pacActor.addVisitedMap(next);
                    LocationPath nextPath = new LocationPath(next);
                    nextPath.setParent(path);
                    locationQueue.addLast(nextPath);
                }
            }
        }
        return new LinkedList<>();
    }
}
