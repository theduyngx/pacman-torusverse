package pacman;
import static pacman.LiveActor.*;

import ch.aplu.jgamegrid.Location;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;


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
     * The State of the game, representing the board with locations of all hash actors.
     * Hash actors are actors that are directly relevant to pathfinding, including the pacman,
     * and all mandatory items - pills and gold.
     */
    private static class State implements Comparable<State>{
        private final int hash;
        LocationPath path;

        /**
         * The state constructor.
         * @param path       the path to reach the state
         * @param hashActors the map of hash actors
         */
        private State(LocationPath path, HashMap<HashLocation, Item> hashActors) {
            hash = hashActors.keySet().hashCode();
            this.path = path;
        }
        @Override
        public int hashCode() {
            return hash;
        }
        @Override
        public int compareTo(State other) {
            return Integer.compare(path.size, other.path.size);
        }
    }

    /**
     * Location path, includes the location itself and its parent, hence forming a path.
     */
    private static class LocationPath {
        private final Location location;
        private LocationPath parent = null;
        private int size;

        private LocationPath(Location location) {
            this.location = location;
            size = 0;
        }
        private void setParent(LocationPath parent) {
            this.parent = parent;
            size += parent.size;
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
            LocationPath locationPath = this;
            while (locationPath != null) {
                path.addFirst(locationPath.location);
                locationPath = locationPath.parent;
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


    /**
     * Proceed with a move.
     * @param pacActor the pacman actor
     * @param next     the location to move to
     */
    public void proceedMove(PacActor pacActor, Location next) {
        Action action = new Action(pacActor.getLocation(), next, HashLocation.get(hashActors, next));
        actionQueue.addFirst(action);
        HashLocation.delete(hashActors, next);
        HashLocation.delete(hashActors, pacActor.getLocation());
        pacActor.setLocation(next);
        pacActor.addVisitedMap(next);
        HashLocation.put(hashActors, next, null);
    }


    /**
     * Undo a move.
     * @param pacActor the pacman actor
     */
    public void undoMove(PacActor pacActor) {
        Action action = actionQueue.removeFirst();
        HashLocation.delete(hashActors, pacActor.getLocation());
        pacActor.setLocation(action.previous);
        pacActor.removeVisitedMap(action.next);
        HashLocation.put(hashActors, action.next, action.itemAtNext);
        HashLocation.put(hashActors, pacActor.getLocation(), null);
    }


    /**
     * A-star pathfinding algorithm.
     * @param pacActor the pacman actor
     * @return         the optimal path to obtain all pills
     */
    public LinkedList<Location> aStar(PacActor pacActor) {
        // all hash actors, including pacman and all mandatory items
        hashActors = pacActor.getManager().getMandatoryItems();
        HashLocation.put(hashActors, pacActor.getLocation(), null);

        // open set, g costs, f costs, and discovered map relative to the state
        PriorityQueue<State> openMin        = new PriorityQueue<>();
        HashMap<State, Integer> gCost       = new HashMap<>();
        HashMap<State, Integer> fCost       = new HashMap<>();
        HashMap<State, Boolean> discovered  = new HashMap<>();

        // get the current state
        LocationPath currPath = new LocationPath(pacActor.getLocation());
        State state = new State(currPath, hashActors);
        openMin.add(state);
        discovered.put(state, true);
        gCost.put(state, currPath.size);
        fCost.put(state, currPath.size);

        // until the open set is empty
        while (! openMin.isEmpty()) {
            state = openMin.remove();
            discovered.remove(state);
            currPath = state.path;

            // or that the state is goal state
            if (hashActors.size() <= 1) {
                while (! actionQueue.isEmpty()) undoMove(pacActor);
                return currPath.getPath();
            }

            // for each neighboring node
            ArrayList<Location> nextLocations = getAllMoves(pacActor);
            for (Location next : nextLocations) {
                proceedMove(pacActor, next);
                LocationPath nextPath = new LocationPath(next);

                // get the accumulated g cost and compare that with the current g cost of neighbor state
                int gCostAccumulate = gCost.get(state) + 1;
                State nextState = new State(nextPath, hashActors);
                if (! gCost.containsKey(nextState) || gCostAccumulate < gCost.get(nextState) + 1) {
                    gCost.put(nextState, gCostAccumulate);
                    fCost.put(nextState, gCostAccumulate + heuristic(hashActors));
                    nextState.path.size = fCost.get(nextState);

                    // update open set and mark new state as discovered
                    if (! discovered.containsKey(nextState)) {
                        discovered.put(nextState, true);
                        openMin.add(nextState);
                    }
                }
            }
        }
        while (! actionQueue.isEmpty()) undoMove(pacActor);
        return new LinkedList<>();
    }


    /**
     * Heuristic function for A-star.
     * @param hashActors the map of all hash actors
     * @return           the heuristic value
     */
    public int heuristic(HashMap<HashLocation, Item> hashActors) {
        // IMPORTANT: find the real distance to the closest fruit = z --> bfs
        // NOT-TOO-IMPORTANT: find the number of fruit 'lines' = y
        // the number of fruits = x

        // h = x + y + z
        return hashActors.size() - 1;
    }


    public LinkedList<Location> dfsLimited(PacActor pacActor, LocationPath path, int depth) {
        if (hashActors.size() <= 1)
            return path.getPath();
        else if (depth <= 0)
            return null;

        ArrayList<Location> nextLocations = getAllMoves(pacActor);
        for (Location next : nextLocations) {
            proceedMove(pacActor, next);
            LocationPath nextPath = new LocationPath(next);
            nextPath.setParent(path);

            LinkedList<Location> ret = dfsLimited(pacActor, nextPath, depth-1);
            if (ret != null)
                return nextPath.getPath();
            undoMove(pacActor);
        }
        return null;
    }

    public LinkedList<Location> ids(PacActor pacActor) {
        // all hash actors, including pacman and all mandatory items
        hashActors = pacActor.getManager().getMandatoryItems();
        HashLocation.put(hashActors, pacActor.getLocation(), null);

        LinkedList<Location> result;
        int depth = 0;
        while (true) {
            result = dfsLimited(pacActor, new LocationPath(pacActor.getLocation()), depth);
            if (result != null)
                return result;
            depth++;
        }
    }
}
