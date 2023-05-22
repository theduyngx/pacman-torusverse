package pacman;
import static pacman.LiveActor.*;

import ch.aplu.jgamegrid.Location;

import java.util.*;


/**
 * LEVEL CHECKING:
 * 1. Check the number of pacActors (cannot exceed 1, if 0 then go to Level editor)
 * 2. Beware of the log file when running game in background
 * 3. Portal validity, each must corresponds to another and cannot have 2 portals of same color (factory method)
 * 4. Path finding to check if any pill/gold is unreachable
 */
public class PathFinding {
    private final LinkedList<Action> actionQueue = new LinkedList<>();
    private HashMap<HashLocation, Item> hashActors = new HashMap<>();
    private record Action(Location previous, Location next, Item itemAtNext) {}

    /**
     * Location path, includes the location itself and its parent, hence forming a path.
     */
    private static class LocationPath {
        private final Location location;
        private LocationPath child = null;
        private int size;
        private int childSize;

        private LocationPath(Location location) {
            this.location = location;
            size = 1;
            childSize = 0;
        }
        private void setChild(LocationPath child) {
            this.child = child;
            size -= childSize;
            childSize = child.size;
            size += childSize;
        }

        @Override
        public int hashCode() {
            return new HashLocation(location).hashCode();
        }

        /**
         * Get the path to the current location path object.
         */
        private LinkedList<Location> getPath() {
            LinkedList<Location> path = new LinkedList<>();
            LocationPath locationPath = this.child; // skip the root
            while (locationPath != null) {
                path.addLast(locationPath.location);
                locationPath = locationPath.child;
            }
            return path;
        }
    }


    /**
     * Get all possible moves a live actor can make from their given position.
     * @param actor the live actor
     * @return      the list of locations live actor can move to
     */
    public ArrayList<Location> getAllMoves(LiveActor actor) {
        ArrayList<Location> moves = new ArrayList<>();
        Location next;
        double oldDirection = actor.getDirection();
        int[] turn_angles = new int[]{RIGHT_TURN_ANGLE, LEFT_TURN_ANGLE, FORWARD_TURN_ANGLE, BACK_TURN_ANGLE};
        for (int turnAngle : turn_angles) {
            actor.setDirection(oldDirection);
            actor.turn(turnAngle);
            next = actor.getNextMoveLocation();
            if (actor.canMove(next)) moves.add(next);
        }
        return moves;
    }

    private void assignActorMap(PacActor pacActor) {
        // all hash actors, including pacman and all mandatory items
        if (hashActors.size() == 0) {
            hashActors = pacActor.getManager().getMandatoryItems();
            if (! HashLocation.contain(hashActors, pacActor.getLocation()))
                HashLocation.put(hashActors, pacActor.getLocation(), null);
        }
    }


    /**
     * Proceed with a move.
     * @param pacActor the pacman actor
     * @param next     the location to move to
     * @return         whether pacman has eaten a mandatory item
     */
    public boolean proceedMove(PacActor pacActor, Location next) {
        if (pacActor == null || next == null)
            return false;
        Action action = new Action(pacActor.getLocation(), next, HashLocation.get(hashActors, next));
        actionQueue.addFirst(action);

        boolean eaten = HashLocation.delete(hashActors, next);
        HashLocation.delete(hashActors, pacActor.getLocation());
        pacActor.setLocation(next);
        HashLocation.put(hashActors, next, null);

        return eaten;
    }


    /**
     * Undo a move.
     * @param pacActor the pacman actor
     */
    public void undoMove(PacActor pacActor) {
        Action action = actionQueue.removeFirst();
        HashLocation.delete(hashActors, pacActor.getLocation());
        pacActor.setLocation(action.previous);
        HashLocation.delete(hashActors, action.next);
        if (action.itemAtNext != null)
            HashLocation.put(hashActors, action.next, action.itemAtNext);
        HashLocation.put(hashActors, pacActor.getLocation(), null);
    }

    public void undoAll(PacActor pacActor) {
        while (! actionQueue.isEmpty())
            undoMove(pacActor);
    }


    /// ISSUE: probably path getting added so null doesn't actually stop the run completely
    public LocationPath dfsLimited(PacActor pacActor, LocationPath path, int depth) {
        // initial stopping conditions
        if (hashActors.size() <= 1) {
            System.out.println("NO WAY");
            return path;
        }
        else if (depth <= 0)
            return null;

        // for every possible move for pacman
        ArrayList<Location> nextLocations = getAllMoves(pacActor);
        for (Location next : nextLocations) {

            // the idea is that if it finds the closest pill to eat, that is the way
            boolean eaten = proceedMove(pacActor, next);
            LocationPath nextPath = new LocationPath(next);
            path.setChild(nextPath);
            if (eaten) {
                System.out.println("YES WAY - path size = " + path.size);
                return path;
            }

            // call it recursively if only required
            LocationPath recursivePath = dfsLimited(pacActor, nextPath, depth-1);
            if (recursivePath != null) {
                System.out.println("SOME WAY - path size = " + path.size);
//                nextPath.setChild(recursivePath);
                return path;
            }

            // undo only if move is not outright beneficial
            undoMove(pacActor);
        }
        return null;
    }


    /**
     * TODO: What we must do instead is to KEEP FINDING THE CLOSEST ITEM!!!
     * Essentially, as soon as IDS finds the closest item, it will return immediately.
     * This will be repeatedly called.
     */
    public LocationPath idsSingle(PacActor pacActor) {
        // all hash actors, including pacman and all mandatory items
        assignActorMap(pacActor);
        LocationPath path = new LocationPath(pacActor.getLocation());

        int depth = 1;
        while (true) {
            System.out.println("depth = " + depth);
            LocationPath curr = dfsLimited(pacActor, path, depth);
            if (curr != null) {
                return curr;
            }
            depth++;
        }
    }


    public LinkedList<Location> idsFull(PacActor pacActor) {
        // all hash actors, including pacman and all mandatory items
        assignActorMap(pacActor);

        LinkedList<Location> result = new LinkedList<>();
        while (hashActors.size() > 1) {

            System.out.println(hashActors.size());

            LocationPath path = idsSingle(pacActor);
            result.addAll(path.getPath());
//            proceedMovePath(pacActor, path);
        }
        undoAll(pacActor);
        return result;
    }
}
