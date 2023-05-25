package game;
import game.Game.Dimension;
import game.utility.GameCallback;
import game.utility.PropertiesLoader;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
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
    // PacMan
    private PacActor pacActor;
    private Actor killedPacActor;

    // PacActor Positions for Errors
    private final ArrayList<Location> pacActorLocations;

    // hashmap of monsters with their initial location as key
    private final ArrayList<Monster> monsters;
    // hashmap of all items with their location as key
    private final HashMap<HashLocation, Item> items;
    // hashmap of all walls with their location as key
    private final HashMap<HashLocation, Integer> walls;
    // hashmap of portals with their respective position
    private final HashMap<HashLocation, Portal> portals;


    // the constructor for all the portals
    private final PortalFactory portalFactory;

    // game callback
    private final GameCallback gameCallback;

    // current number of pills and gold pieces, which indicate whether player has won or not
    private int numMandatoryItems = 0;
    private boolean isMultiverse = false;
    private final Game game;

    /**
     * Constructor for ObjectManager.
     */
    public ObjectManager(Game game, GameCallback gameCallback) {
        this.game              = game;
        this.gameCallback      = gameCallback;
        this.pacActorLocations = new ArrayList<>();
        this.monsters          = new ArrayList<>();
        this.items             = new HashMap<>();
        this.walls             = new HashMap<>();
        this.portals           = new HashMap<>();
        this.portalFactory     = PortalFactory.getInstance();
    }

    public Dimension getDimension() {
        return game.getDimension();
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
     * Get all the recorded PacMan locations. Used for checking for errors regarding the number
     * of PacMan locations recorded
     * @return ArrayList of PacMan Locations
     * @see    PacActor
     */
    protected ArrayList<Location> getPacActorLocations() { return pacActorLocations; }

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
                = items.entrySet()
                .stream()
                .filter(e -> !(e.getValue() instanceof Ice))
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        ));
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
     * Get the portals map
     * @return the map of all portals
     * @see Portal
     */
    protected HashMap<HashLocation, Portal> getPortals() {
        return portals;
    }

    /**
     * Get the portal factory
     * @return a factory for constructing all the portals in the map
     * @see PortalFactory
     */
    protected PortalFactory getPortalFactory() { return portalFactory; }

    /**
     * Get the number of pills and gold pieces left in the game. Hence, used to detect winning condition.
     * @return the number of pills and gold pieces left in the game
     */
    protected int getNumMandatoryItems() {
        return numMandatoryItems;
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
            numMandatoryItems--;
    }


    /**
     * Parse the game's properties file.
     * @param properties specified properties file
     */
    public void parseProperties(Properties properties) {
        // random seed
        int seed = Integer.parseInt(properties.getProperty(PropertiesLoader.SEED));
        isMultiverse = properties.getProperty(PropertiesLoader.VERSION).contains(PropertiesLoader.IS_MULTIVERSE);
        // parse pacman (comment out to test for torus)
        pacActor.setAuto(Boolean.parseBoolean(properties.getProperty(
                pacActor.getName() + PropertiesLoader.AUTO_EXTENSION))
        );
        /// SET SEED AND SLOW DOWN TO REDUCE GAME DIFFICULTY
        pacActor.setSeed(seed);
        pacActor.setSlowDown(LiveActor.SLOW_DOWN);
        for (Monster monster : monsters) {
            monster.setSeed(seed);
            monster.setSlowDown(LiveActor.SLOW_DOWN);
        }
    }


    /**
     * Instantiate all game's objects, given the XML file.
     * @param xmlFile the specified XML file
     */
    public void instantiateAll(String xmlFile) {
        try {
            XMLParser.parseXML(xmlFile, this);
        } catch (ParserConfigurationException | IOException | SAXException exception) {
            exception.printStackTrace();
        }
        numMandatoryItems = getMandatoryItems().size();
        pacActor = new PacActor(this);
        if (pacActorLocations.size() > 0) {
            pacActor.setInitLocation(pacActorLocations.get(0));
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
        numMandatoryItems = 0;
        if (pacActor != null) pacActor.removeSelf();
        if (killedPacActor != null) killedPacActor.removeSelf();
        for (Monster monster : monsters)
            monster.removeSelf();
        monsters.clear();
        for (Map.Entry<HashLocation, Item> entry : items.entrySet())
            entry.getValue().removeSelf();
        items.clear();
        for (Map.Entry<HashLocation, Portal> entry : portals.entrySet())
            entry.getValue().removeSelf();
        portals.clear();
        pacActorLocations.clear();
        walls.clear();
    }
}