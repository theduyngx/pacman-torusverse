package pacman.src;
import ch.aplu.jgamegrid.*;
import java.util.*;

/**
 * Based on skeleton code for SWEN20003 Project, Semester 2, 2022, The University of Melbourne.
 * Monster abstract class extended from abstract LiveActor class.
 * @see LiveActor
 */
public abstract class Monster extends LiveActor {

    // step sizes
    public static final int AGGRESSIVE_STEP_SIZE = 2;

    /**
     * Monster type enumeration. Each monster type has a boolean value indicating whether it is exclusive
     * to the extended multiverse game or not.
     * <ul>
     *     <li>Troll  - not exclusive to multiverse
     *     <li>TX5    - not exclusive to multiverse
     *     <li>Alien  - exclusive to multiverse
     *     <li>Orion  - exclusive to multiverse
     *     <li>Wizard - exclusive to multiverse
     * </ul>
     */
    public enum MonsterType {
        Troll(false),
        TX5(false),
        Alien(true),
        Orion(true),
        Wizard(true);
        public final boolean inMultiverse;
        MonsterType(boolean inMultiverse) {
            this.inMultiverse = inMultiverse;
        }
    }

    // time-related constants
    public static final int SECOND_TO_MILLISECONDS = 1000;
    public static final int AGGRAVATE_TIME = 3;
    // if it has stopped moving or not
    private boolean stopMoving = false;

    /**
     * Monster constructor.
     * @param isRotatable   if monster is rotatable
     * @param directory     sprite image directory
     * @param numSprites    number of sprites
     */
    public Monster(ObjectManager manager, boolean isRotatable, String directory, int numSprites) {
        super(manager, isRotatable, directory, numSprites);
        assert manager != null;
    }

    /**
     * Get the object manager.
     * @return the object manager
     */
    @Override
    public ObjectManager getManager() {
        assert super.getManager() != null;
        return super.getManager();
    }

    /**
     * Set the monster type to monster.
     * @param type the monster type
     */
    public void setType(MonsterType type) {
        setName(type.toString());
    }

    /**
     * Overridden method for setting monster's seed.
     * @param seed specified seed
     */
    @Override
    protected void setSeed(int seed) {
        getRandomizer().setSeed(seed);
    }

    /**
     * Set monster to either stop or continue/start moving.
     * @param stopMoving boolean indicating if monster stops moving or not
     */
    protected void setStopMoving(boolean stopMoving) {
        this.stopMoving = stopMoving;
    }

    /**
     * Stops monster's movement for a specified number of seconds.
     * @param seconds number of seconds monster stops moving
     */
    protected void stopMoving(int seconds) {
        setStopMoving(true);
        Timer timer = new Timer(); // Instantiate Timer Object
        final Monster monster = this;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                monster.setStopMoving(false);
            }
        }, (long) seconds * SECOND_TO_MILLISECONDS);
    }

    /**
     * Speed up monster's movement by a constant factor for a specified number of seconds.
     * @param seconds number of seconds monster speeds up
     */
    public void speedUp(int seconds) {
        this.setStepSize(AGGRESSIVE_STEP_SIZE);
        Timer timer = new Timer();
        final Monster monster = this;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                monster.setStepSize(LiveActor.NORMAL_STEP_SIZE);
            }
        }, (long) seconds * SECOND_TO_MILLISECONDS);
    }


    /**
     * Overridden act method from Actor class for monster to act within the game.
     * @see Actor
     */
    @Override
    public void act() {
        if (stopMoving) return;
        moveApproach();
        int DIRECTION_EXCEED = 150;
        int DIRECTION_PRECEDE = 210;
        boolean enable = getDirection() > DIRECTION_EXCEED && getDirection() < DIRECTION_PRECEDE;
        setHorzMirror(!enable);

        // Record changes in position to game
        getGameCallback().monsterLocationChanged(this);
    }


    /**
     * Adding itself to be an 'official' part of the game, viz. an actor of the game. Overridden
     * from Movable interface.
     * @param game the game
     * @see        Movable
     */
    @Override
    public void putActor(Game game) {
        game.addActor(this, getInitLocation(), Location.NORTH);
    }


    /**
     * Overridden moveApproach method from LiveActor class for monsters within the game
     * @see LiveActor
     */
    @Override
    public void moveApproach() {
        Location newLocation = nextMonsterLocation(this.getStepSize());
        if (newLocation == null) {
            newLocation = nextMonsterLocation(LiveActor.NORMAL_STEP_SIZE);
        }

        // If you really cannot move, just stand still
        if (newLocation == null) return;

        this.setLocation(newLocation);
    }

    /**
     * Abstract method for specific movement behavior of monster types
     */
    protected abstract Location nextMonsterLocation(int stepSize);
}
