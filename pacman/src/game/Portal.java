package game;

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
    private final PortalColor color;


    /**
     * The portal type, enumerated by its color.
     */
    public enum PortalColor {
        White(XMLParser.PORTAL_WHITE_TILE),
        Yellow(XMLParser.PORTAL_YELLOW_TILE),
        DarkGold(XMLParser.PORTAL_DARK_GOLD_TILE),
        DarkGrey(XMLParser.PORTAL_DARK_GREY_TILE);
        public final String color;

        /**
         * Portal type constructor.
         * @param portalColor the portal color
         */
        PortalColor(String portalColor) {
            this.color = portalColor;
        }
        private static final HashMap<String, PortalColor> map = new HashMap<>(values().length, 1);

        static {
            for (PortalColor c: values()) map.put(c.color, c);
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
        public static PortalColor of(String color) {
            PortalColor result = map.get(color);
            if (result == null)
                throw new IllegalArgumentException("Invalid color name " + color);
            return result;
        }
    }


    /**
     * Constructor for the Portal when no pair exists yet
     * @param sprite    Portal Sprite displayed in the game
     */
    public Portal(String sprite, Location location, PortalColor color) {
        super(sprite);
        this.staticLocation = location;
        this.color = color;
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
     * Get the Portal's color.
     * @return the portal's color
     */
    public PortalColor getColor() {
        return color;
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