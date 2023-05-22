/**
 * Portal class used to transport LiveActors to the other paired portal
 * @see InanimateActor
 */
public class Portal extends InanimateActor {
    private String portalSprite;
    private final Portal portalPair;
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
        this.portalSprite = sprite;
        this.pairPortal = pair;
        // Set the pair of the other portal
        pair.setPortalPair(this);
    }

    /**
     * Necessary setter function to pair portals together
     * @param pair  Portal instance paired with another
     */
    public void setPortalPair(Portal pair) {
        this.portalPair = pair;
    }
}