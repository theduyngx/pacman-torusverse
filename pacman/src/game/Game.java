package game;
import game.utility.GameCallback;

import ch.aplu.jgamegrid.*;
import java.awt.*;
import java.util.Map;
import java.util.Properties;


/**
 * Based on skeleton code for SWEN20003 Project, Semester 2, 2022, The University of Melbourne.
 * The Game class represents the entire PacMan game. It is responsible for putting items and
 * actors onto its own grid, as well as running the game.
 * @see GameGrid
 *
 * @author The Duy Nguyen            - 1100548 (theduyn@student.unimelb.edu.au)
 * @author Ramon Javier L. Felipe VI - 1233281 (rfelipe@student.unimelb.edu.au)
 * @author Jonathan Chen Jie Kong    - 1263651 (jonathanchen@student.unimelb.edu.au)
 */
public class Game extends GameGrid implements Runnable {
    // draw grid colors
    private final static Color COLOR_LOSE = Color.red;
    private final static Color COLOR_WIN = Color.yellow;
    private final static Color COLOR_BACKGROUND = Color.white;
    private final static Color COLOR_WALL = Color.gray;
    protected final static Color COLOR_SPACE = Color.lightGray;

    // win/lose messages
    private final static String LOSE_MESSAGE = "GAME OVER";
    private final static String WIN_MESSAGE = "YOU WIN";

    // game running constants
    private final static int SIMULATION_PERIOD = 100;
    private final static int KEY_REPEATED_PERIOD = 150;
    private final static String GAME_TITLE = "[PacMan in the TorusVerse]";
    public final static String RUN_TITLE = "[PacMan in the TorusVerse] Current score: ";
    private final static int DELAY_RUN = 10;
    private final static int DELAY_AFTER_RUN = 120;

    // game grid
    public final static int DEFAULT_WIDTH = 20;
    public final static int DEFAULT_HEIGHT = 11;
    protected final static int STRETCH_RATE = 2;
    private final static int CELL_SIZE = 20 * STRETCH_RATE;

    // object manager
    private final ObjectManager manager;
    private boolean start = false;
    private final GGBackground bg;
    private final Properties properties;
    private final Dimension dimension;
    private STATUS status;


    /**
     * The game's enumerated status - win, lose, or neither.
     */
    public enum STATUS {
        WIN, LOSE, NA
    }

    /**
     * The game's dimensions.
     * @param width  grid's width
     * @param height grid's height
     */
    public record Dimension(int width, int height) {}


    /**
     * Game class constructor.
     * @param properties   properties object read from properties file for instantiating actors and items
     * @param gameCallback the game callback for updating log
     * @see   GameCallback
     */
    public Game(Dimension dimension, Properties properties, GameCallback gameCallback) {
        // Setup game
        super(dimension.width, dimension.height, CELL_SIZE, false);
        this.manager    = new ObjectManager(this, gameCallback);
        this.properties = properties;
        this.dimension  = dimension;

        // set up game window
        setSimulationPeriod(SIMULATION_PERIOD);
        setTitle(GAME_TITLE);
        bg = getBg();
        setKeyRepeatPeriod(KEY_REPEATED_PERIOD);
    }


    /**
     * Reset the game's state by re-putting actors and re-setting up the game settings
     * and object manager. Called in Controller when the game needs to be reset and ready
     * for another play.
     */
    public void reset(String xmlFile) {
        // remove all actors
        status = STATUS.NA;
        manager.removeAll();
        setTitle(GAME_TITLE);

        // parse properties and instantiate objects
        manager.instantiateAll(xmlFile);
        manager.parseProperties(properties);

        // instantiate actors
        drawGrid(bg);
        putInanimateObjects();
        putLiveActors();
        manager.setMonstersStopMoving();
    }

    /**
     * Run the game. Upon running, all actors and items will be put to the game, and it will continually
     * check for a winning / losing condition until either one is met.
     */
    public void run() {
        if (!start) return;
        manager.setMonstersStartMoving();

        // Run the game
        doRun();
        show();

        // run the game until win / lose condition satisfies
        PacActor pacActor = manager.getPacActor();
        addKeyListener(pacActor);
        boolean hasPacmanEatAllPills, hasPacmanBeenHit;
        do {
            hasPacmanBeenHit     = pacActor.collideMonster();
            hasPacmanEatAllPills = manager.getNumMandatoryItems() <= 0;
            delay(DELAY_RUN);
        } while (! hasPacmanBeenHit && ! hasPacmanEatAllPills);
        delay(DELAY_AFTER_RUN);

        // upon game over
        Location loc = pacActor.getLocation();
        manager.setMonstersStopMoving();
        pacActor.removeSelf();
        if (hasPacmanBeenHit) {
            bg.setPaintColor(COLOR_LOSE);
            status = STATUS.LOSE;
            setTitle(LOSE_MESSAGE);
            addActor(manager.getKilledPacActor(), loc);
            manager.getGameCallback().endOfGame(LOSE_MESSAGE);
        }
        else {
            bg.setPaintColor(COLOR_WIN);
            status = STATUS.WIN;
        }
        doPause();
    }


    /**
     * Called when the player has truly won the game.
     */
    public void win() {
        manager.getGameCallback().endOfGame(WIN_MESSAGE);
        setTitle(WIN_MESSAGE);
        doPause();
    }


    public Dimension getDimension() {
        return dimension;
    }

    /**
     * Get the game's status - whether the player has won, lost, or neither.
     * @return the game's status for the player
     */
    public STATUS getStatus() {
        return status;
    }

    /**
     * Get the object manager.
     * @return the object manager
     */
    protected ObjectManager getManager() {
        return manager;
    }

    /**
     * Get the game start status (if True then the game has officially started).
     * @return True if the game has officially started
     */
    public boolean getStart() {
        return start;
    }

    /**
     * Set the game start status (if True then the game has officially started).
     * @param start True if the game has officially started
     */
    public void setStart(boolean start) {
        this.start = start;
    }


    /**
     * Draw the game's grid. The grid includes empty space and walls.
     * @param bg background object for grid
     * @see      GGBackground
     */
    private void drawGrid(GGBackground bg) {
        // set the background
        bg.clear(COLOR_WALL);
        bg.setPaintColor(COLOR_BACKGROUND);

        // draw the maze (its border and items)
        for (int y = 0; y < dimension.height; y++) {
            for (int x = 0; x < dimension.width; x++) {
                bg.setPaintColor(COLOR_BACKGROUND);
                Location location = new Location(x, y);
                // space
                if (! HashLocation.contain(manager.getWalls(), location))
                    bg.fillCell(location, COLOR_SPACE);
                // wall -> added to wall map in manager
                else {
                    HashLocation.put(manager.getWalls(), location, 1);
                    bg.fillCell(location, COLOR_WALL);
                }
            }
        }
    }


    /**
     * Putting all inanimate objects to game. As the name suggests, inanimate objects are statically
     * located, and once put to the game will not be moving.
     */
    public void putInanimateObjects() {
        // items
        for (Map.Entry<HashLocation, Item> entry : manager.getItems().entrySet()) {
            Location location = entry.getKey().location();
            Item item = entry.getValue();
            item.putActor(bg, this, location);
        }
        // portals
        for (Map.Entry<HashLocation, Portal> entry : manager.getPortals().entrySet())
            entry.getValue().putActor(bg, this, entry.getKey().location());
    }

    /**
     * Putting all live actors to game. Actors once put to the game will exist within the game as well as the
     * grid, and it will also be visualized to the background.
     * @see Monster
     */
    public void putLiveActors() {
        // monsters
        for (int i=0; i < manager.getMonsters().size(); i++) {
            Monster monster = manager.getMonsters().get(i);
            monster.putActor(this);
        }
        // pacman
        if (manager.getPacActorLocations().size() > 0)
            manager.getPacActor().putActor(this);
    }
}