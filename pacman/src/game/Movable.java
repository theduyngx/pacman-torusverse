package game;
import ch.aplu.jgamegrid.Location;


/**
 * Movable interface representing any movable object within a game.
 * @see Game
 * @see LiveActor
 */
public interface Movable {
    /**
     * Interface method to put live actor to the game.
     * @param game the game
     */
    void putActor(Game game);

    /**
     * Interface method checking whether a movable game object can move to a specified location or not.
     * @param location the specified location
     * @return         boolean value indicating whether object can move to specified location
     * @see            Location
     */
    boolean canMove(Location location);

    /**
     * Interface move approach that changes depending on the movable game object.
     */
    void moveApproach();

    /**
     * Interface method add a specified location to the list of visited locations. Visited locations is
     * important in determining movable game object behaviors.
     * @param location specified visited location
     * @see            Location
     */
    void addVisitedList(Location location);

    /**
     * Check whether location has been visited within a cycle by movable game object or not. This is important
     * since much of the movable game object behaviors is determined by their visited (and otherwise) locations.
     * @param location the specified location
     * @return         boolean indicating whether location has been visited within a cycle or not.
     * @see            Location
     */
    boolean notVisited(Location location);
}
