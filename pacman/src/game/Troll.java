package game;
import game.utility.PropertiesLoader;
import ch.aplu.jgamegrid.*;


/**
 * Troll class extended from abstract parent Monster.
 * Enemies in the game whose movement is completely random.
 * @see Monster
 */
public class Troll extends Monster {
    // Type of monster needed for GameCallback
    private static final MonsterType TYPE = MonsterType.Troll;

    // Variables used for super's constructor
    public static final int NUM_TROLL_IMAGES = 1;
    public static final String DIRECTORY = PropertiesLoader.PATH + "m_troll.gif";

    /**
     * Troll constructor
     * @param manager stores locations of all game objects
     */
    public Troll(ObjectManager manager) {
        super(manager, false, DIRECTORY, NUM_TROLL_IMAGES);
        assert manager != null;
        setType(TYPE);
    }

    /**
     * Moves troll to its next location, determination of movement is completely random
     */
    @Override
    protected Location nextMonsterLocation(int stepSize) {
        // Tracks whether a location was found
        Location finalLoc = null;

        // First get a random direction to go to (left or right)
        double oldDirection = this.getDirection();
        int sign = this.getRandomizer().nextDouble() < 0.5 ? 1 : -1;
        this.turn(sign*RIGHT_TURN_ANGLE);
        Location next = nextLocation();

        if (this.canMove(stepSize)) finalLoc = next;

        // If collision occurs going first given direction, try other direction
        else {
            // Try to move forward
            this.setDirection(oldDirection);
            next = nextLocation();
            if (this.canMove(stepSize)) finalLoc = next;

            else {
                // Check if you can go the opposite turn, either left or right
                this.turn(sign * LEFT_TURN_ANGLE);
                next = nextLocation();
                if (this.canMove(stepSize)) finalLoc = next;

                // If nothing really worked, just go backwards
                else {
                    this.setDirection(oldDirection);
                    this.turn(BACK_TURN_ANGLE);
                    next = nextLocation();
                    if (this.canMove(stepSize)) finalLoc = next;
                }
            }
        }

        // Tell game to change monster's location and store this as visited
        return finalLoc;
    }
}