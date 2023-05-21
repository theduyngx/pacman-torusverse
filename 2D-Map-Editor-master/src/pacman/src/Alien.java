package pacman.src;
import ch.aplu.jgamegrid.Location;
import pacman.src.utility.PropertiesLoader;

import java.util.ArrayList;


/**
 * Aliens class extended from abstract parent Monster.
 * Enemies in the game who move ONLY to get closer to Pacman, and can also move diagonally.
 * @see Monster
 */
public class Alien extends Monster {
    // Name of class needed for GameCallback
    private static final MonsterType TYPE = MonsterType.Alien;
    // Need these variables for implementation with super constructor
    public static final int NUM_ALIEN_IMAGES = 1;
    public static final String DIRECTORY = PropertiesLoader.PATH + "m_alien.gif";

    /**
     * Alien constructor.
     * @param manager stores locations of all game objects
     * @see           ObjectManager
     */
    public Alien(ObjectManager manager) {
        super(manager, false, DIRECTORY, NUM_ALIEN_IMAGES);
        assert manager != null;
        setType(TYPE);
    }


    /**
     * Moves Alien to its next location, purely determined by which 8 neighboring locations
     * it can move to and are closest to Pacman. Overridden from Monster.
     */
    @Override
    protected Location nextMonsterLocation(int stepSize) {
        // Aliens pick from the directions it can walk to, and choose one that's closest to pacman
        ArrayList<Location> possibleMoves = new ArrayList<>();
        int minDistance = Integer.MAX_VALUE;
        Location pacmanLocation = getManager().getPacActor().getLocation();

        for (Location.CompassDirection dir: Location.CompassDirection.values()) {
            Location currLocation = this.getLocation().getAdjacentLocation(dir, stepSize);
            int distanceToPacman = currLocation.getDistanceTo(pacmanLocation);

            // ties mean to randomly pick from all tying directions
            if (this.canMove(currLocation) && distanceToPacman <= minDistance) {
                if (distanceToPacman < minDistance) {
                    minDistance = distanceToPacman;
                    possibleMoves = new ArrayList<>();
                }
                possibleMoves.add(currLocation);
            }
        }

        // If there are no possible moves, return null
        if (possibleMoves.isEmpty()) return null;

        // Randomly pick a direction from all possible minimum distance directions
        int listIndex = this.getRandomizer().nextInt(0, possibleMoves.size());
        return possibleMoves.get(listIndex);
    }
}