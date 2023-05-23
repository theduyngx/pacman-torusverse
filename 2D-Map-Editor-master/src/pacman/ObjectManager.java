package pacman;
import ch.aplu.jgamegrid.Actor;
import pacman.utility.GameCallback;
import pacman.utility.PropertiesLoader;
import ch.aplu.jgamegrid.Location;

import java.util.*;
import java.util.stream.Collectors;


/**
 * ObjectManager class to manage all objects, animate or inanimate (viz. item or live actor), especially
 * their instantiations and locations. Anything that has to do with checking every actor for a specific
 * task uniformly will be a responsibility of ObjectManager, since it has access to all Actors, as well as
 * other grid-related objects.
 * <p>
 * As such, ObjectManager can be frequently used to deal with a specific Actor checking the 'state' of
 * every other actor.
 * @see Game
 * @see Item
 * @see LiveActor
 */
public class ObjectManager {
    // constant initial seed
    private final static int INIT_SEED = 30006;

    // PacMan
    private PacActor pacActor;
    private Actor killedPacActor;
    // hashmap of monsters with their initial location as key
    private final ArrayList<Monster> monsters;
    // hashmap of all items with their location as key
    private final HashMap<HashLocation, Item> items;
    // hashmap of all walls with their location as key
    private final HashMap<HashLocation, Integer> walls;

    // the game
    private final Game game;
    // game callback
    private final GameCallback gameCallback;
    // random seed
    private int seed = INIT_SEED;
    // current number of pills and gold pieces, which indicate whether player has won or not
    private int numPillsAndGold = 0;
    private boolean isMultiverse = false;

    /**
     * Constructor for ObjectManager.
     * @see Game
     */
    public ObjectManager(Game game, GameCallback gameCallback) {
        assert game != null;
        this.game = game;
        this.gameCallback = gameCallback;
        this.monsters = new ArrayList<>();
        this.items = new HashMap<>();
        this.walls = new HashMap<>();
    }

    /**
     * Get the game object; used to retrieve game's grid, which either to update grid's cell or to
     * get the information about the grid's border to disallow actors moving out of bound.
     * @return the game
     */
    protected Game getGame() {
        return game;
    }

    /**
     * Get the game callback; used by live actors to update their activities to log.
     * @return the game callback
     * @see    GameCallback
     */
    protected GameCallback getGameCallback() {
        return gameCallback;
    }

    /**
     * Get the player PacMan. This is primarily used for checking collisions between PacMan and monsters.
     * @return player PacMan
     * @see    PacActor
     */
    protected PacActor getPacActor() {
        return pacActor;
    }

    /**
     * Get all monsters.
     * @return a list of all the monsters in the game
     * @see    Monster
     */
    protected ArrayList<Monster> getMonsters() {
        return monsters;
    }

    /**
     * Get all items currently still in the game.
     * @return a hashmap where the key is the items' locations, and value being the items
     * @see    HashLocation
     * @see    Item
     */
    protected HashMap<HashLocation, Item> getItems() {
        return items;
    }

    /**
     * Get all gold and pill items currently still in the game.
     * @return a hashmap where the key is the items' locations, and value being the items
     * @see    HashLocation
     * @see    Item
     */
    protected HashMap<HashLocation, Item> getMandatoryItems() {
        Map<HashLocation, Item> map
                = items.entrySet().stream().
                        filter(
                             e -> !(e.getValue() instanceof Ice)).collect(
                        Collectors.toMap(
                            Map.Entry::getKey, Map.Entry::getValue)
                );
        return new HashMap<>(map);
    }

    /**
     * Get all walls.
     * @return a hashmap where the key is the walls' locations, and value being the walls
     * @see    HashLocation
     */
    protected HashMap<HashLocation, Integer> getWalls() {
        return walls;
    }

    /**
     * Get the number of pills and gold pieces left in the game. Hence, used to detect winning condition.
     * @return the number of pills and gold pieces left in the game
     */
    protected int getNumPillsAndGold() {
        return numPillsAndGold;
    }

    /**
     * Get the version of the game
     * @return a boolean representing if the game is simple or a multiverse
     */
    protected boolean isMultiverse() {
        return isMultiverse;
    }

    /**
     * Decrementing the number of pill and gold pieces when PacMan eats one of the pieces.
     * Hence, used in eatItem of PacActor. It will also check if a specified item is of instance
     * Gold or Pill, and if not then it will not decrement.
     * @param item specified eaten item
     * @see        Item
     */
    protected void decrementNumPillAndGold(Item item) {
        if (item instanceof Gold || item instanceof Pill)
            numPillsAndGold--;
    }


    /**
     * Parse properties that do not relate to a live actor instantiation. This includes the seed, edible
     * items, among others available in the properties file.
     * @param properties the specified properties
     * @see   Properties
     */
    public void parseInanimateActor(Properties properties) {
        seed = Integer.parseInt(properties.getProperty("seed"));
        isMultiverse = properties.getProperty("version").contains("multiverse");

        // concern only about locations of edible items
        ArrayList<InanimateActor.BlockType> blockTypes =
                new ArrayList<>(Arrays.asList(InanimateActor.BlockType.values()));

        // for each of said item type
        for (InanimateActor.BlockType blockType : blockTypes) {
            String name = blockType.toString();
            String property_name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

            // we check if item is included in properties file
            if (properties.containsKey(property_name)) {
                String[] itemLocations = properties.getProperty(property_name).split(";");

                // parse its locations
                for (String pL : itemLocations) {
                    String[] pos = pL.split(",");
                    int posX = Integer.parseInt(pos[0]);
                    int posY = Integer.parseInt(pos[1]);
                    Location location = new Location(posX, posY);
                    Item item = switch(blockType) {
                        case PILL -> new Pill();
                        case GOLD -> new Gold();
                        case ICE  -> new Ice();
                        default   -> null;
                    };

                    // if null, then it is not an item
                    if (item == null) continue;

                    // add to item hashmaps and set game grid's cell
                    HashLocation.put(items, location, item);
                    getGame().getGrid().setCell(location, blockType);
                    if (blockType == InanimateActor.BlockType.PILL || blockType == InanimateActor.BlockType.GOLD)
                        numPillsAndGold++;
                }
            }
        }
    }


    /**
     * Instantiate the pacman actor. Called in Game constructor.
     * @param properties properties to parse for pacman
     * @see   Properties
     * @see   PacActor
     */
    protected void instantiatePacActor(Properties properties) {
        // instantiate pacman
        pacActor = new PacActor(this);
        pacActor.setSeed(seed);
        pacActor.setSlowDown(LiveActor.SLOW_DOWN);

        // parse pacman (comment out to test for torus)
//        pacActor.setAuto(Boolean.parseBoolean(properties.getProperty(
//                pacActor.getName() +
//                PropertiesLoader.AUTO_EXTENSION))
//        );
        String[] pacManLocations = properties.getProperty(
                pacActor.getName() +
                PropertiesLoader.LOCATION_EXTENSION
        ).split(",");
        int pacManX = Integer.parseInt(pacManLocations[0]);
        int pacManY = Integer.parseInt(pacManLocations[1]);
        pacActor.setInitLocation(new Location(pacManX, pacManY));
    }


    /**
     * Instantiating monsters. Called in Game constructor.
     * @param properties properties to parse for monsters
     * @see   Properties
     */
    protected void instantiateMonsters(Properties properties) {
        // for each monster type
        ArrayList<Monster.MonsterType> types = new ArrayList<>(Arrays.asList(Monster.MonsterType.values()));
        for (Monster.MonsterType type : types) {
            // check if monster type is valid (as in, if type only exists in multiverse but property
            // states otherwise, then we ignore)
            if (type.inMultiverse && !isMultiverse) continue;
            String name = type.toString();
            String property_name = name + PropertiesLoader.LOCATION_EXTENSION;

            // valid entry
            if (properties.containsKey(property_name) && !properties.getProperty(property_name).equals("")) {
                String[] locations = properties.getProperty(property_name).split(";");

                // get all locations of monster
                for (String loc : locations) {
                    String[] pos = loc.split(",");
                    int posX = Integer.parseInt(pos[0]);
                    int posY = Integer.parseInt(pos[1]);
                    Location location = new Location(posX, posY);
                    Monster monster = switch(type) {
                        case TX5    -> new TX5(this);
                        case Troll  -> new Troll(this);
                        case Orion  -> new Orion(this);
                        case Alien  -> new Alien(this);
                        case Wizard -> new Wizard(this);
                    };

                    // set location and add itself to monster list
                    monster.setInitLocation(location);
                    this.monsters.add(monster);

                    /// SET SEED AND SLOW DOWN TO REDUCE GAME DIFFICULTY
                    monster.setSeed(seed);
                    monster.setSlowDown(LiveActor.SLOW_DOWN);
                }
            }
        }
    }


    /**
     * Instantiate the items in the grid and put them in their respective hashmaps. Called in Game constructor.
     * @param grid the game grid so that the items can be drawn onto
     * @see   PacManGameGrid
     */
    protected void instantiateObjects(PacManGameGrid grid) {
        for (int col = 0; col < grid.getNumVerticalCells(); col++)
            for (int row = 0; row < grid.getNumHorizontalCells(); row++) {
                InanimateActor.BlockType itemType = grid.getMazeArray()[col][row];

                // ignore if location is already occupied
                Location location = new Location(row, col);
                if (HashLocation.contain(items, location)) continue;

                // otherwise add
                switch (itemType) {
                    case PILL -> {
                        Pill pill = new Pill();
                        HashLocation.put(items, location, pill);
                        numPillsAndGold++;
                    }
                    case GOLD -> {
                        Gold gold = new Gold();
                        HashLocation.put(items, location, gold);
                        numPillsAndGold++;
                    }
                    case ICE -> {
                        Ice ice = new Ice();
                        HashLocation.put(items, location, ice);
                    }
                }
            }
    }


    /**
     * Set all monsters to stop moving; used when game is over (win/lose condition is met).
     * @see Monster
     */
    protected void setMonstersStopMoving() {
        for (Monster monster: monsters)
            monster.setStopMoving(true);
    }

    /**
     * Set all monsters to start moving; used when starting the game.
     * @see Monster
     */
    protected void setMonstersStartMoving() {
        for (Monster monster: monsters)
            monster.setStopMoving(false);
    }


    /**
     * Get the killed pacActor; naturally called when the game is over and pacActor was killed.
     * This is for the Game to the specific actor with given killed sprite onto itself.
     * @return the killed pacActor
     */
    protected Actor getKilledPacActor() {
        killedPacActor = new Actor(PacActor.KILLED_SPRITE);
        return killedPacActor;
    }


    /**
     * Remove all actors from the game and resetting all lists. Called in Game.reset() to
     * do a full reset on the game's level.
     */
    protected void removeAll() {
        if (pacActor != null) pacActor.removeSelf();
        if (killedPacActor != null) killedPacActor.removeSelf();
        for (Monster monster : monsters)
            monster.removeSelf();
        monsters.clear();
        for (Map.Entry<HashLocation, Item> entry : items.entrySet())
            entry.getValue().removeSelf();
        items.clear();
        walls.clear();
    }
}
