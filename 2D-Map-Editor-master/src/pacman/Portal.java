package pacman;

import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;

/**
 * Portal class used to transport LiveActors to the other paired portal
 * @see InanimateActor
 */
public class Portal extends InanimateActor {
    private String portalSprite;
    private Portal portalPair;
    private static final String PORTAL_NAME = "portal";

    /**
     * Constructor for the Portal when no pair exists yet
     * @param sprite    Portal Sprite displayed in the game
     */
    public Portal(String sprite) {
        super(sprite);
        this.portalSprite = sprite;
    }

    /**
     * Constructor for the portal when a pair exists already
     * @param sprite    Portal Sprite displayed in the game
     * @param pair      Instance to be paired with new portal
     */
    public Portal(String sprite, Portal pair) {
        super(sprite);
        this.portalSprite = sprite;
        this.portalPair = pair;
        // Set the pair of the other portal
        pair.setPortalPair(this);
    }

    /**
     * Overwritten putItem method for Portals, portal puts
     * itself into the game
     * @param bg        background of game grid
     * @param game      the game
     * @param location  actor's location
     */
    @Override
    protected void putActor(GGBackground bg, Game game, Location location) {
        game.addActor(this, location);
    }

    /**
     * Necessary setter function to pair portals together
     * @param pair  Portal instance paired with another
     */
    public void setPortalPair(Portal pair) {
        this.portalPair = pair;
    }
}