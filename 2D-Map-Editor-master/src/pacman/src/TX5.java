package pacman.src;
import ch.aplu.jgamegrid.*;
import pacman.src.utility.PropertiesLoader;


/**
 * TX5 class extended from Monster class. These are enemies that always move towards Pacman.
 * If unable to do so, they move randomly.
 * @see Monster
 */
public class TX5 extends Monster {
    // Name of class needed for GameCallback
    private static final MonsterType TYPE = MonsterType.TX5;

    // Required variables for super constructor
    public static final int NUM_TX5_IMAGES = 1;
    public static final String DIRECTORY = PropertiesLoader.PATH + "m_tx5.gif";
    private static final int INIT_STOP_TIME = 5;

    /**
     * TX5 Constructor
     * @param manager stores locations of all game objects
     */
    public TX5(ObjectManager manager) {
        super(manager, false, DIRECTORY, NUM_TX5_IMAGES);
        assert manager != null;
        setType(TYPE);
        // TX5 is special in that it sets itself to not move initially
        this.stopMoving(INIT_STOP_TIME);
    }

    /**
     * Moves TX5 to its next location, determination of movement is purely to get closer to Pacman;
     * otherwise resorts to random movement. Overridden from Monster.
     */
    @Override
    protected Location nextMonsterLocation(int stepSize) {
        // Tracks whether a location was found
        Location finalLoc = null;

        // With TX5, need to base direction to move on the position of pacman
        Location pacLocation = getManager().getPacActor().getLocation();
        double oldDirection = this.getDirection();
        Location.CompassDirection compassDir = getLocation().get4CompassDirectionTo(pacLocation);
        this.setDirection(compassDir);

        // This marks the direction nearest to pacman
        Location next = this.getLocation().getAdjacentLocation(this.getDirection(), stepSize);

        // Only go to this direction if you can move here, and if it wasn't visited yet
        if (this.canMove(this.getDirection(), stepSize) && this.notVisited(next)) finalLoc = next;

        // If it can't move here, has to move to a random spot
        else {
            int sign = this.getRandomizer().nextDouble() < 0.5 ? 1 : -1;
            this.setDirection(oldDirection);
            this.turn(sign*RIGHT_TURN_ANGLE);
            next = this.getLocation().getAdjacentLocation(this.getDirection(), stepSize);

            // Check if we can turn this direction
            if (this.canMove(this.getDirection(), stepSize))
                finalLoc = next;

            else {
                // Try move forward
                this.setDirection(oldDirection);
                next = this.getLocation().getAdjacentLocation(this.getDirection(), stepSize);
                if (this.canMove(this.getDirection(), stepSize)) finalLoc = next;

                // Try turn the other direction
                else {
                    this.setDirection(oldDirection);
                    this.turn(sign*LEFT_TURN_ANGLE);
                    next = this.getLocation().getAdjacentLocation(this.getDirection(), stepSize);
                    if (this.canMove(this.getDirection(), stepSize)) finalLoc = next;

                    // Just move backwards
                    else {
                        this.setDirection(oldDirection);
                        this.turn(BACK_TURN_ANGLE);
                        next = this.getLocation().getAdjacentLocation(this.getDirection(), stepSize);
                        if (this.canMove(this.getDirection(), stepSize)) finalLoc = next;
                    }
                }
            }
        }

        // Store location in visited list if not null
        if (finalLoc != null) this.addVisitedList(finalLoc);
        return finalLoc;
    }
}