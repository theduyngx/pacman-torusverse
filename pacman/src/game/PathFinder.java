package game;
import static game.LiveActor.*;

import ch.aplu.jgamegrid.Location;
import java.util.*;
import java.util.stream.Collectors;


/**
 * PathFinder class with various different pathfinding algorithms serving different purposes.
 * This includes an optimal pathfinding algorithm, IDS, to search PacActor's next move during
 * auto-mode, as well as a level checking algorithm, DFS, to obtain all the reachable space.
 */
public class PathFinder {
    // the queue of proceeded actions (for undoing purposes)
    private final LinkedList<Action> actionQueue = new LinkedList<>();

    // the map of all hash actors
    private HashMap<HashLocation, Item> hashActors = new HashMap<>();

    // structure for an action taken (for caching to optimize undo)
    private record Action(Location previous, Location next, Item itemAtNext) {}


    /**
     * Location path, includes the location itself and its parent, hence forming a path.
     * Essentially, it takes the form of a linked list.
     */
    private static class LocationPath {
        private final Location location;
        private LocationPath child = null;
        private int size;
        private int childSize;

        /**
         * Location path constructor.
         * @param location the specified location
         */
        private LocationPath(Location location) {
            this.location = location;
            size = 1;
            childSize = 0;
        }

        /**
         * Set the child for location path.
         * @param child the specified child
         */
        private void setChild(LocationPath child) {
            this.child = child;
            size -= childSize;
            childSize = child.size;
            size += childSize;
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

        @Override
        public int hashCode() {
            return new HashLocation(location).hashCode();
        }
    }


    /**
     * Get all possible moves a live actor can make from their given position.
     * @param actor the live actor
     * @return      the list of locations live actor can move to
     */
    private ArrayList<Location> getAllMoves(LiveActor actor) {
        ArrayList<Location> moves = new ArrayList<>();
        Location next;
        double oldDirection = actor.getDirection();
        int[] turn_angles = new int[]{RIGHT_TURN_ANGLE, LEFT_TURN_ANGLE, FORWARD_TURN_ANGLE, BACK_TURN_ANGLE};
        for (int turnAngle : turn_angles) {
            actor.setDirection(oldDirection);
            actor.turn(turnAngle);
            next = actor.nextLocation();
            if (actor.canMove(next)) moves.add(next);
        }
        return moves;
    }

    /**
     * Assigning the hash actors map for PathFinder.
     * @param pacActor the pacman actor
     */
    private void assignActorMap(PacActor pacActor) {
        // all hash actors, including pacman and all mandatory items
        if (hashActors.size() == 0) {
            hashActors = pacActor.getManager().getMandatoryItems();
            if (! HashLocation.contain(hashActors, pacActor.getLocation()))
                HashLocation.put(hashActors, pacActor.getLocation(), null);
        }
    }


    /**
     * Proceed with a move. This will update the hash actor map and the action queue.
     * @param pacActor the pacman actor
     * @param next     the location to move to
     * @return         whether pacman has eaten a mandatory item
     */
    private boolean proceedMove(PacActor pacActor, Location next) {
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
     * Undo a move. This will return the hash actor map and the action queue to its previous state.
     * @param pacActor the pacman actor
     */
    private void undoMove(PacActor pacActor) {
        Action action = actionQueue.removeFirst();
        HashLocation.delete(hashActors, pacActor.getLocation());
        pacActor.setLocation(action.previous);
        HashLocation.delete(hashActors, action.next);
        if (action.itemAtNext != null)
            HashLocation.put(hashActors, action.next, action.itemAtNext);
        HashLocation.put(hashActors, pacActor.getLocation(), null);
    }

    /**
     * Undo all actions taken so far by PathFinder.
     * @param pacActor the pacman actor
     */
    private void undoAll(PacActor pacActor) {
        while (! actionQueue.isEmpty())
            undoMove(pacActor);
    }


    /**
     * DFS limited depth, utility function for IDS.
     * @param pacActor the pacman actor
     * @param path     the current path to reach state
     * @param depth    the specified limited depth
     * @return         NULL if no result, or the path to take if a result exists
     */
    private LocationPath dfsLimited(PacActor pacActor, LocationPath path, int depth) {
        // initial stopping conditions
        if (hashActors.size() <= 1)
            return path;
        else if (depth <= 0)
            return null;

        // for every possible move for pacman
        ArrayList<Location> nextLocations = getAllMoves(pacActor);
        for (Location next : nextLocations) {

            // the idea is that if it finds the closest pill to eat, that is the way
            boolean eaten = proceedMove(pacActor, next);
            LocationPath nextPath = new LocationPath(next);
            path.setChild(nextPath);
            if (eaten)
                return path;

            // call it recursively if only required
            LocationPath recursivePath = dfsLimited(pacActor, nextPath, depth-1);
            if (recursivePath != null)
                return path;

            // undo only if move is not outright beneficial
            undoMove(pacActor);
        }
        return null;
    }


    /**
     * IDS for a single next path instead of the entire game
     * @param pacActor the pacman actor
     * @return         the path to be taken
     */
    public LocationPath idsSinglePath(PacActor pacActor) {
        assignActorMap(pacActor);
        LocationPath path = new LocationPath(pacActor.getLocation());

        // iteratively deepening the depth until an item is reached
        int depth = 1;
        while (true) {
            LocationPath curr = dfsLimited(pacActor, path, depth);
            if (curr != null)
                return curr;
            depth++;
        }
    }


    /**
     * IDS to find the locations for pacman to go to for each single next item;
     * used in PacActor's moveApproach method.
     * @param pacActor the pacman actor
     * @return         the list of next locations to move to
     */
    public LinkedList<Location> idsSingle(PacActor pacActor) {
        LocationPath pathSingle = idsSinglePath(pacActor);
        undoAll(pacActor);
        return pathSingle.getPath();
    }


    /**
     * DFS algorithm to check for all reachable spaces in the game. Useful for level checking
     * to ensure all mandatory items are reachable.
     * @param pacActor the pacman actor
     * @return         the list of reachable locations
     */
    public ArrayList<Location> dfsGreedyCheck(PacActor pacActor) {
        // keep track of the stack and visited locations
        LinkedList<Location> stack = new LinkedList<>();
        stack.addFirst(pacActor.getLocation());
        HashMap<HashLocation, Boolean> visited = new HashMap<>();

        // until the stack is empty
        while (! stack.isEmpty()) {
            Location location = stack.removeFirst();
            proceedMove(pacActor, location);

            // if location is not yet visited, mark as visited
            if (! HashLocation.contain(visited, location)) {
                HashLocation.put(visited, location, true);

                // and add its child nodes to stack
                ArrayList<Location> nextLocations = getAllMoves(pacActor);
                for (Location next : nextLocations)
                    stack.addFirst(next);
            }
        }
        // undo all moves
        undoAll(pacActor);

        // return the key set of the visited map
        return (ArrayList<Location>)
                visited.keySet()
                       .stream()
                       .map(HashLocation::location)
                       .collect(Collectors.toList());
    }
}
