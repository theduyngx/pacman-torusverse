/**
 * Pill class extended from abstract Item class. An item in the game that is not consumed.
 * Instead, it transports Pacman to the location of the paired portal.
 * @see Item
 */
public class Portal extends Item {
    private String portalSprite;
    private final Portal portalPair;
    private static final String PORTAL_NAME = "portal";

    // Constructor for portals created without a pair
    public Portal(String sprite) {
        this.portalSprite = sprite;
    }

    // Overloaded constructor for portals that will
    // be constructed with a pair
    public Portal(String sprite, Portal pair) {
        this.portalSprite = sprite;
        this.pairPortal = pair;
        // Set the pair of the other portal
        pair.setPortalPair(this);
    }

    // Need this to set the
    public setPortalPair(Portal pair) {
        this.portalPair = pair;
    }
}