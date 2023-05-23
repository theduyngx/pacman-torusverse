package pacman;

import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;

import java.util.HashMap;


/**
 * Portal class used to transport LiveActors to the other paired portal
 * @see InanimateActor
 */
public class Portal extends InanimateActor {
    private final Location staticLocation;
    private Portal portalPair;
    private static final String PORTAL_NAME = "portal"; // might need for callback


    /**
     * The portal type, enumerated by its color.
     */
    public enum PortalType {
        White(XMLParser.PORTAL_WHITE_TILE),
        Yellow(XMLParser.PORTAL_YELLOW_TILE),
        DarkGold(XMLParser.PORTAL_DARK_GOLD_TILE),
        DarkGrey(XMLParser.PORTAL_DARK_GREY_TILE);
        public final String color;

        /**
         * Portal type constructor.
         * @param portalColor the portal color
         */
        PortalType(String portalColor) {
            this.color = portalColor;
        }
        private static final HashMap<String, PortalType> map = new HashMap<>(values().length, 1);

        static {
            for (PortalType c: values()) map.put(c.color, c);
        }

        /**
         * Get the color sprite path for the portal type in question.
         * @return the sprite path
         */
        public String getColorSprite() {
            return switch (this) {
                case White    -> "data/i_portalWhiteTile.png";
                case Yellow   -> "data/j_portalYellowTile.png";
                case DarkGold -> "data/k_portalDarkGoldTile.png";
                case DarkGrey -> "data/l_portalDarkGrayTile.png";
            };
        }

        /**
         * Portal type color check with string.
         * @param color specified color
         * @return      portal type
         */
        public static PortalType of(String color) {
            PortalType result = map.get(color);
            if (result == null)
                throw new IllegalArgumentException("Invalid color name " + color);
            return result;
        }
    }


    /**
     * Constructor for the Portal when no pair exists yet
     * @param sprite    Portal Sprite displayed in the game
     */
    public Portal(String sprite, Location location) {
        super(sprite);
        this.staticLocation = location;
    }

    /**
     * Get the portal pair.
     * @return the pair
     */
    public Portal getPortalPair() {
        return portalPair;
    }

    /**
     * Get the portal's static location.
     * @return the portal's location
     */
    public Location getStaticLocation() {
        return staticLocation;
    }

    /**
     * Necessary setter function to pair portals together
     * @param pair  Portal instance paired with another
     */
    protected void setPortalPair(Portal pair) {
        this.portalPair = pair;
        pair.portalPair = this;
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
}