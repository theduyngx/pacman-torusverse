package pacman;
import static pacman.LiveActor.*;

import ch.aplu.jgamegrid.Location;
import java.util.ArrayList;
import java.util.LinkedList;


/**
 * LEVEL CHECKING:
 * 1. Check the number of pacActors (cannot exceed 1, if 0 then go to Level editor)
 * 2. Beware of the log file when running game in background
 * 3. Portal validity (each must corresponds to another and cannot have 2 portals of same color (factory method)
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

    public boolean bfs(PacActor pacActor) {
        ObjectManager manager = pacActor.getManager();
        LinkedList<Location> locationQueue = new LinkedList<>();
        locationQueue.addLast(pacActor.getLocation());
        while (! locationQueue.isEmpty()) {
            Location loc = locationQueue.removeFirst();
            pacActor.moveWithVisited(loc);
            if (manager.getNumPillsAndGold() <= 0)
                return true;
            ArrayList<Location> nextLocations = getAllMoves(pacActor);
            for (Location next : nextLocations) {
                if (! pacActor.hasVisited(next)) {
                    pacActor.addVisitedMap(next);
                    locationQueue.addLast(next);
                }
            }
        }
        return false;
    }
}
