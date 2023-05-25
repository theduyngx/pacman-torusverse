package game;
import game.utility.PropertiesLoader;

import ch.aplu.jgamegrid.*;
import java.awt.*;


/**
 * Gold class extended from abstract Item class.
 * @see Item
 */
public class Gold extends Item {
    // properties
    private static final String DIRECTORY = PropertiesLoader.PATH + "gold.png";
    private static final int GOLD_SCORE = 5;
    private static final String GOLD_NAME = "gold";

    /**
     * Constructor for Gold. It will set its own score, and call Item's constructor with its own
     * sprite image directory.
     */
    public Gold() {
        super(DIRECTORY);
        setScore(GOLD_SCORE);
        setName(GOLD_NAME);
    }

    /**
     * Overridden putItem method, where gold puts itself to the game.
     * @param bg        background of game grid
     * @param game      the game
     * @param location  the current gold item's location
     * @see             GGBackground
     * @see             Game
     * @see             Location
     */
    @Override
    protected void putActor(GGBackground bg, Game game, Location location) {
        bg.setPaintColor(Color.yellow);
        game.addActor(this, location);
    }

    @Override
    public Item deepCopy() {
        return new Gold();
    }

    /**
     * Overridden method signalling object manager to aggravate monsters.
     * @param manager object manager
     * @see           ObjectManager
     */
    @Override
    protected void signalManager(ObjectManager manager) {
        // assert that player is in fact at the location of item
        if (matchPacmanLocation(manager))
            // trigger signal
            for (Monster monster : manager.getMonsters())
            {
                if (manager.isMultiverse())
                {
                    monster.speedUp(Monster.AGGRAVATE_TIME);
                    // monster is Orion, then we want Orion to know that this gold piece is already eaten
                    if (monster instanceof Orion orion)
                        HashLocation.put(
                                orion.getGoldPacmanAte(),
                                this.getLocation(),
                                true
                        );
                }
            }
    }
}