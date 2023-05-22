package pacman;

/**
 * Factory class for construction of portals; ensures that each portal
 * is paired with each other.
 * @see Portal
 */

public class PortalFactory {
    private static String[] portals;
    public static PortalFactory instance;

    /**
     * Necessary setter function to put all possible
     * portals
     * @param possiblePortals   All possible portals, as String png file names
     */
    public void setPortals(String[] possiblePortals) {
        portals = possiblePortals;
    }

    /**
     * Constructor Function for all the portals of the game
     * @return  List of Portals to put in the board
     */
    public Portal[] makePortals() {
        // First make the portal list, always double the length
        // of the portal hashmap
        Portal[] portalList = new Portal[portals.length*2];
        int i=0;

        // Empty the hashmap since we are only constructing
        // portals once
        for (String portal: portals) {
            // Now make the portals
            portalList[i] = new Portal(portal);
            portalList[i+1] = new Portal(portal, portalList[i]);
            i+=2;
        }

        return portalList;
    }

    /**
     * Creates instance of PortalFactory for portal creation
     * @return  PortalFactory instance
     */
    public static synchronized PortalFactory getInstance() {
        if (instance == null) {
            instance = new PortalFactory();
        }
        return instance;
    }
}