public enum PortalType {
    White,
    Yellow,
    DarkGold,
    DarkGray;

    public String getStringName() {
        switch (this) {
            case White: return "data/i_portalWhiteTile.png";
            case Yellow: return "data/j_portalYellowTile.png";
            case DarkGold: return "data/k_portalDarkGoldTile.png";
            case DarkGray: return "data/l_portalDarkGrayTile.png";
            default: {
                assert false;
            }
        }
        return null;
    }
}