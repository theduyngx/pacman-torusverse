package pacman;

import ch.aplu.jgamegrid.*;
import pacman.utility.GameCallback;

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
    public final static Color COLOR_LOSE = Color.red;
    public final static Color COLOR_WIN = Color.yellow;
    public final static Color COLOR_BACKGROUND = Color.white;
    public final static Color COLOR_WALL = Color.gray;
    public final static Color COLOR_SPACE = Color.lightGray;

    // win/lose messages
    public final static String LOSE_MESSAGE = "GAME OVER";
    public final static String WIN_MESSAGE = "YOU WIN";

    // game running constants
    private final static int SIMULATION_PERIOD = 100;
    private final static int KEY_REPEATED_PERIOD = 150;
    private final static String GAME_TITLE = "[PacMan in the TorusVerse]";
    private final static int DELAY_RUN = 10;
    private final static int DELAY_AFTER_RUN = 120;

    // game grid
    public final static int STRETCH_RATE = 2;
    public final static int CELL_SIZE = 20 * STRETCH_RATE;
    private final static int NUM_HORIZONTAL_CELLS = 20;
    private final static int NUM_VERTICAL_CELLS = 11;
    private final PacManGameGrid grid;

    // object manager
    private final ObjectManager manager;
    private boolean start = false;


    /**
     * Game class constructor.
     * @param properties properties object read from properties file for instantiating actors and items
     * @see              Properties
     */
    public Game(Properties properties, GameCallback gameCallback) {
        // Setup game
        super(NUM_HORIZONTAL_CELLS, NUM_VERTICAL_CELLS, CELL_SIZE, false);
        this.grid = new PacManGameGrid(NUM_HORIZONTAL_CELLS, NUM_VERTICAL_CELLS);
        this.manager = new ObjectManager(this, gameCallback);

        // parse properties and instantiate objects
        manager.parseInanimateActor(properties);
        manager.instantiatePacActor(properties);
        manager.instantiateObjects(grid);

        // instantiate actors
        manager.instantiateMonsters(properties);
        addKeyRepeatListener(manager.getPacActor());
        setKeyRepeatPeriod(KEY_REPEATED_PERIOD);

        // set up game window
        setSimulationPeriod(SIMULATION_PERIOD);
        setTitle(GAME_TITLE);
    }

    /**
     * Get the game grid.
     * @return the game grid
     * @see    PacManGameGrid
     */
    public PacManGameGrid getGrid() {
        return grid;
    }


    /**
     * Set the game start status (if True then the game has officially started).
     * @param start True if the game has officially started
     */
    public void setStart(boolean start) {
        this.start = start;
    }

    /**
     * Run the game. Upon running, all actors and items will be put to the game, and it will continually
     * check for a winning / losing condition until either one is met.
     */
    public void run() {
        if (!start) return;
        GGBackground bg = getBg();
        drawGrid(bg);

        // put actors onto the game
        putMonsters();
        putPacActor();

        // Run the game
        doRun();
        show();

        // run the game until win / lose condition satisfies
        PacActor pacActor = manager.getPacActor();
        boolean hasPacmanEatAllPills, hasPacmanBeenHit;
        putItems(bg);
        do {
            hasPacmanBeenHit     = pacActor.collideMonster();
            hasPacmanEatAllPills = manager.getNumPillsAndGold() <= 0;
            delay(DELAY_RUN);
        } while (! hasPacmanBeenHit && ! hasPacmanEatAllPills);
        delay(DELAY_AFTER_RUN);

        // upon game over
        Location loc = pacActor.getLocation();
        manager.setMonstersStopMoving();
        pacActor.removeSelf();
        String title;
        if (hasPacmanBeenHit) {
            bg.setPaintColor(COLOR_LOSE);
            title = LOSE_MESSAGE;
            addActor(new Actor(PacActor.KILLED_SPRITE), loc);
        }
        else {
            bg.setPaintColor(COLOR_WIN);
            title = WIN_MESSAGE;
        }
        setTitle(title);
        manager.getGameCallback().endOfGame(title);
        doPause();
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
        for (int y = 0; y < NUM_VERTICAL_CELLS; y++) {
            for (int x = 0; x < NUM_HORIZONTAL_CELLS; x++) {
                bg.setPaintColor(COLOR_BACKGROUND);
                Location location = new Location(x, y);
                // space
                if (grid.getCell(location) != InanimateActor.BlockType.ERROR)
                    bg.fillCell(location, COLOR_SPACE);
                // wall -> added to wall map in manager
                if (grid.getCell(location) == InanimateActor.BlockType.WALL) {
                    HashableLocation.putLocationHash(manager.getWalls(), location, 1);
                    bg.fillCell(location, COLOR_WALL);
                }
            }
        }
    }


    /**
     * Putting all items to game. Items once put to the game will exist within the game as well as the
     * grid, and it will also be visualized to the background.
     * @param background the background
     * @see              GGBackground
     * @see              Item
     */
    public void putItems(GGBackground background) {
        for (Map.Entry<HashableLocation, Item> entry : manager.getItems().entrySet()) {
            Location location = entry.getKey().location();
            Item item = entry.getValue();
            item.putActor(background, this, location);
        }
    }

    /**
     * Putting all monsters to game. Monsters once put to the game will exist within the game as well as the
     * grid, and it will also be visualized to the background.
     * @see Monster
     */
    public void putMonsters() {
        for (int i=0; i<manager.getMonsters().size(); i++) {
            Monster monster = manager.getMonsters().get(i);
            monster.putActor(this);
        }
    }

    /**
     * Putting PacMan to game. Similar to put monsters, PacMan will also be added to the game in the same
     * manner.
     * @see PacActor
     */
    public void putPacActor() {
        manager.getPacActor().putActor(this);
    }
}