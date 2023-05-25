package pacman;
import ch.aplu.jgamegrid.Location;
import pacman.utility.PropertiesLoader;

import java.util.ArrayList;


/**
 * Wizard class extended from Monster class.
 * @see Monster
 */
public class Wizard extends Monster {
    // Name of class needed for GameCallback
    private static final MonsterType TYPE = MonsterType.Wizard;

    // Required variables for super constructor
    public static final int NUM_WIZARD_IMAGES = 1;
    public static final String DIRECTORY = PropertiesLoader.PATH + "m_wizard.gif";

    // Constants needed for wizard class
    public static final int LIST_START = 0;
    public static final int BEYOND_WALL = 1;

    /**
     * Wizard constructor
     * @param manager stores locations of all game objects
     */
    public Wizard(ObjectManager manager) {
        super(manager, false, DIRECTORY, NUM_WIZARD_IMAGES);
        assert manager != null;
        setType(TYPE);
    }

    /**
     * Moves Wizard to its next location, movement is randomly selected from its 8 neighboring locations,
     * but also has the ability to walk through walls. Overridden from Monster.
     */
    @Override
    protected Location nextMonsterLocation(int stepSize) {
        Location finalLoc = null; // This checks if we even can return a direction

        // Get the possibleDirections then add each direction to directionValues
        Location.CompassDirection[] possibleDirections = Location.CompassDirection.values();
        ArrayList<Integer> directionValues = new ArrayList<>();
        for (Location.CompassDirection dir : possibleDirections)
            directionValues.add(dir.getDirection());

        // Loop until a location is set; randomly pick a direction or if it has exhausted all of them
        while (!directionValues.isEmpty()) {
            int currIndex = this.getRandomizer().nextInt(LIST_START, directionValues.size());
            int currDirection = directionValues.get(currIndex);

            if (this.canMove(stepSize)) {
                finalLoc = this.getLocation().getAdjacentLocation(currDirection, stepSize);
                break;
            }

            // Even when not movable, it might be able to go to adjacent block if space beyond wall is valid
            else {
                // Furious or not, wizard only looks 1 step after chosen location to see if it's wall or not
                Location beyondWallLocation = this.getLocation().getAdjacentLocation(currDirection,
                        stepSize+BEYOND_WALL);
                // Must also check if the location right before the wall is walkable
                if (this.canMove(beyondWallLocation) && this.canMove(stepSize-BEYOND_WALL)) {
                    finalLoc = beyondWallLocation;
                    break;
                }
            }
            directionValues.remove(currIndex);
        }

        return finalLoc;
    }
}