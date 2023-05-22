package pacman;
import ch.aplu.jgamegrid.*;
import pacman.utility.PropertiesLoader;

import java.awt.event.KeyEvent;
import java.util.*;


/**
 * Based on skeleton code for SWEN20003 Project, Semester 2, 2022, The University of Melbourne.
 * PacActor class extended from abstract LiveActor class, implementing a key repeat listener interface.
 * The latter is so that the game responds to player's input.
 * @see LiveActor
 * @see GGKeyRepeatListener
 * @see ObjectManager
 */
public class PacActor extends LiveActor implements GGKeyRepeatListener {
    // properties
    private static final int NUM_SPRITES = 4;
    private static final String DIRECTORY = PropertiesLoader.PATH + "pacpix.gif";
    private static final String PACMAN_NAME = "PacMan";
    public static final String KILLED_SPRITE = PropertiesLoader.PATH + "explosion3.gif";
    private int idSprite = 0;
    private int nbPills = 0;
    private int score = 0;

    // properties related to sequence of moves for pacman in auto mode
    private List<String> propertyMoves = new ArrayList<>();
    private int propertyMoveIndex = 0;
    // if pacman is in auto mode
    private boolean isAuto = false;


    /**
     * PacMan constructor.
     * @param manager the object manager
     */
    public PacActor(ObjectManager manager) {
        super(manager, true, DIRECTORY, NUM_SPRITES);
        assert manager != null;
        setName(PACMAN_NAME);
    }

    /**
     * Set whether PacMan runs in auto mode or player mode.
     * @param auto true if PacMan runs in auto mode, false if otherwise
     */
    protected void setAuto(boolean auto) {
        isAuto = auto;
    }

    /**
     * Set the random seed for PacMan.
     * @param seed specified seed
     */
    @Override
    protected void setSeed(int seed) {
        getRandomizer().setSeed(seed);
    }

    /**
     * Setting player's sequence of moves in auto-movement mode.
     * @param propertyMoveString the string, separated by ',' where each other character represents a
     *                           particular move
     */
    protected void setPropertyMoves(String propertyMoveString) {
        if (propertyMoveString != null)
            this.propertyMoves = Arrays.asList(propertyMoveString.split(","));
    }

    /**
     * Method in key listener.
     * @param keyCode key code represents which key was pressed by player.
     */
    @Override
    public void keyRepeated(int keyCode) {
        if (isAuto) return;
        if (isRemoved())  // Already removed
            return;
        Location next = null;
        switch (keyCode) {
            case KeyEvent.VK_LEFT -> {
                next = getLocation().getNeighbourLocation(Location.WEST);
                setDirection(Location.WEST);
            }
            case KeyEvent.VK_UP -> {
                next = getLocation().getNeighbourLocation(Location.NORTH);
                setDirection(Location.NORTH);
            }
            case KeyEvent.VK_RIGHT -> {
                next = getLocation().getNeighbourLocation(Location.EAST);
                setDirection(Location.EAST);
            }
            case KeyEvent.VK_DOWN -> {
                next = getLocation().getNeighbourLocation(Location.SOUTH);
                setDirection(Location.SOUTH);
            }
        }
        if (next != null && canMove(next))
            moveWithVisited(next);
    }

    /**
     * Overridden act method from Actor class to act within the game.
     * @see Actor
     */
    @Override
    public void act() {
        show(idSprite);
        idSprite++;
        if (idSprite == NUM_SPRITES)
            idSprite = 0;
        if (isAuto)
            moveApproach();
        getGameCallback().pacManLocationChanged(getLocation(), score, nbPills);
    }

    /**
     * Move for PacMan with consideration to the visited map.
     * @param next the next location to move to
     */
    public void moveWithVisited(Location next) {
        setLocation(next);
        eatItem(getManager());
    }

    /**
     * Overridden move approach method for PacMan which is only used when in auto movement mode.
     * Overridden from Movable.
     * @see Movable
     */
    @Override
    public void moveApproach() {
        if (propertyMoves.size() > propertyMoveIndex) {
            followPropertyMoves();
            return;
        }
        Location closestPill = closestPillLocation();
        double oldDirection = getDirection();
        Location.CompassDirection compassDir = getLocation().get4CompassDirectionTo(closestPill);
        Location next = getLocation().getNeighbourLocation(compassDir);
        setDirection(compassDir);
        if (!notVisited(next) || !canMove(next)) {
            int sign = getRandomizer().nextDouble() < 0.5 ? 1 : -1;
            setDirection(oldDirection);
            turn(sign * RIGHT_TURN_ANGLE); // Try to turn left/right
            next = getNextMoveLocation();
            if (! canMove(next)) {
                setDirection(oldDirection);
                next = getNextMoveLocation(); // Try to move forward
                if (! canMove(next)) {
                    setDirection(oldDirection);
                    turn(sign * LEFT_TURN_ANGLE); // Try to turn right/left
                    next = getNextMoveLocation();
                    if (! canMove(next)) {
                        setDirection(oldDirection);
                        turn(BACK_TURN_ANGLE); // Turn backward
                        next = getNextMoveLocation();
                    }
                }
            }
        }
        moveWithVisited(next);
        if (isAuto) addVisitedList(next);
    }

    /**
     * Method for handling PacMan eating an item. Each item will have a different effect upon acquired, and
     * this method will handle that as well.
     * @param manager object manager
     */
    protected void eatItem(ObjectManager manager) {
        Location location = getLocation();
        HashLocation hashLocation = new HashLocation(location);

        // item exists
        if (manager.getItems().containsKey(hashLocation)) {
            Item item = manager.getItems().get(hashLocation);

            // add score (WIP - this shouldn't even be in here)
            if (! (item instanceof Ice)) nbPills++;
            score += item.getScore();
            getManager().decrementNumPillAndGold(item);

            // signals the manager and removes itself
            item.signalManager(manager);
            getBackground().fillCell(location, Game.COLOR_SPACE);
            getGameCallback().pacManEatPillsAndItems(location, item.getName());
            item.removeItem(manager);
        }
        String title = "[PacMan in the Multiverse] Current score: " + score;
        getGameGrid().setTitle(title);
    }

    /**
     * Game over checking - whether PacMan has collided with a monster or not.
     * @return true if collided, false if otherwise.
     */
    public boolean collideMonster() {
        for (Monster monster : getManager().getMonsters())
            if (actorCollide(monster))
                return true;
        return false;
    }

    /**
     * Get the closest location of an item that is either a pill or gold. Used only when in auto mode.
     * @return said closest location
     * @see    Location
     */
    private Location closestPillLocation() {
        int currentDistance = getManager().getGame().getGrid().INF; // set distance to infinity
        Location currentLocation = null;
        for (Map.Entry<HashLocation, Item> entry : getManager().getItems().entrySet()) {
            Item item = entry.getValue();
            if (item instanceof Pill || item instanceof Gold) {
                Location location = entry.getKey().location();
                int distanceToPill = location.getDistanceTo(getLocation());
                if (distanceToPill < currentDistance) {
                    currentLocation = location;
                    currentDistance = distanceToPill;
                }
            }
        }
        return currentLocation;
    }

    /**
     * Let pacman in auto mode follow the moves parsed from properties file. It will read which direction
     * pacman will be heading to, and move accordingly.
     */
    private void followPropertyMoves() {
        if (! isAuto) return;
        String currentMove = propertyMoves.get(propertyMoveIndex);
        switch (currentMove) {
            case PropertiesLoader.RIGHT_DIR -> turn(RIGHT_TURN_ANGLE);
            case PropertiesLoader.LEFT_DIR  -> turn(LEFT_TURN_ANGLE);
            case PropertiesLoader.MOVE_DIR  -> {
                Location next = getNextMoveLocation();
                if (canMove(next))
                    moveWithVisited(next);
            }
        }
        propertyMoveIndex++;
    }


    /**
     * Adding itself to be an 'official' part of the game, viz. an actor of the game.
     * Overridden from Movable.
     * @param game the game
     * @see        Movable
     */
    @Override
    public void putActor(Game game) {
        game.addActor(this, getInitLocation());
    }
}