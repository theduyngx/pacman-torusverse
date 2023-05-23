package pacman;

import pacman.XMLParser;

import java.util.HashMap;

public enum PortalType {
    White(XMLParser.PORTAL_WHITE_TILE),
    Yellow(XMLParser.PORTAL_YELLOW_TILE),
    DarkGold(XMLParser.PORTAL_DARK_GOLD_TILE),
    DarkGrey(XMLParser.PORTAL_DARK_GREY_TILE);

    PortalType(String portalColor) {
        this.color = portalColor;
    }

    private static final HashMap<String, PortalType> map = new HashMap<>(values().length, 1);

    static {
        for (PortalType c: values()) map.put(c.color, c);
    }

    public final String color;

    public String getColorSprite() {
        switch (this) {
            case White: return "data/i_portalWhiteTile.png";
            case Yellow: return "data/j_portalYellowTile.png";
            case DarkGold: return "data/k_portalDarkGoldTile.png";
            case DarkGrey: return "data/l_portalDarkGrayTile.png";
            default: {
                assert false;
            }
        }
        return null;
    }

    public static PortalType of(String color) {
        PortalType result = map.get(color);
        if (result == null) {
            throw new IllegalArgumentException("Invalid color name "+color);
        }
        return result;
    }
}