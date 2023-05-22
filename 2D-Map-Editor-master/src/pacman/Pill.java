package pacman;
import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;
import pacman.utility.PropertiesLoader;

import java.awt.*;


/**
 * Pill class extended from abstract Item class. Item is required to be eaten by pacman, but doesn't have
 * any particular effect other than increasing the points in the game.
 * @see Item
 */
public class Pill extends Item {
    // properties
    private static final String DIRECTORY = PropertiesLoader.PATH + "ice.png";
    private static final int PILL_SCORE = 1;
    private static final String PILL_NAME = "pill";

    /**
     * Constructor for Pill. It will set its own score, and call Item's constructor with its own
     * sprite image directory.
     */
    public Pill() {
        super(DIRECTORY);
        setScore(PILL_SCORE);
        setName(PILL_NAME);
    }

    /**
     * Overridden putItem method, where ice puts itself to the game.
     * @param bg        background of game grid
     * @param game      the game
     * @param location  the current gold item's location
     * @see             GGBackground
     * @see             Game
     * @see             Location
     */
    @Override
    public void putActor(GGBackground bg, Game game, Location location) {
        bg.setPaintColor(Color.white);
        bg.fillCircle(game.toPoint(location), RADIUS);
        game.addActor(this, location);
        hide();
    }

    /**
     * Overridden method to signal manager, although since pill has no effect on monsters, it will do nothing.
     * @param manager the object manager
     */
    @Override
    protected void signalManager(ObjectManager manager) {
        // do nothing
    }
}