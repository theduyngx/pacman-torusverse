package pacman;
import static pacman.LiveActor.*;

import ch.aplu.jgamegrid.Location;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


/**
 * LEVEL CHECKING:
 * 1. Check the number of pacActors (cannot exceed 1, if 0 then go to Level editor)
 * 2. Beware of the log file when running game in background
 * 3. Portal validity, each must corresponds to another and cannot have 2 portals of same color (factory method)
 * 4. Path finding to check if any pill/gold is unreachable
 */
public class PathFinding {
    LinkedList<Action> actionQueue = new LinkedList<>();

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


    private record Action(Location previous, Location next, Item itemAtNext) {
        // ...
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


    public void proceedMove(PacActor pacActor, Location next, HashMap<HashableLocation, Item> items) {
        Action action = new Action(pacActor.getLocation(), next, items.get(new HashableLocation(next)));
        actionQueue.addFirst(action);
        items.remove(new HashableLocation(next));
        pacActor.setLocation(next);
        pacActor.addVisitedMap(next);
    }

    public void undoMove(PacActor pacActor, HashMap<HashableLocation, Item> items) {
        Action action = actionQueue.removeFirst();
        pacActor.setLocation(action.previous);
        pacActor.removeVisitedMap(action.next);
        items.put(new HashableLocation(action.next), action.itemAtNext);
    }


    public LinkedList<Location> bfs(PacActor pacActor) {
        HashMap<HashableLocation, Item> items = pacActor.getManager().getItems();
        LinkedList<LocationPath> locationQueue = new LinkedList<>();
        locationQueue.addLast(new LocationPath(pacActor.getLocation()));

        while (! locationQueue.isEmpty()) {
            LocationPath path = locationQueue.removeFirst();
            proceedMove(pacActor, path.location, items);

            // goal state
            if (items.size() == 0) {
                while (! actionQueue.isEmpty()) undoMove(pacActor, items);
                return LocationPath.getPath(path);
            }

            // for each next unvisited location
            ArrayList<Location> nextLocations = getAllMoves(pacActor);
            for (Location next : nextLocations) {
                if (! pacActor.hasVisited(next)) {
                    pacActor.addVisitedMap(next);
                    LocationPath nextPath = new LocationPath(next);
                    nextPath.setParent(path);
                    locationQueue.addLast(nextPath);
                }
            }
//            undoMove(pacActor, items);
        }
        while (! actionQueue.isEmpty()) undoMove(pacActor, items);
        return new LinkedList<>();
    }
}
