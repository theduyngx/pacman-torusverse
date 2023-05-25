package pacman;
import ch.aplu.jgamegrid.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Factory class for construction of portals; ensures that each portal is paired with each other.
 * It utilizes Singleton pattern with a unique, global point of entry.
 * @see Portal
 */
public class PortalFactory {
    private static PortalFactory instance;
    private PortalFactory() {}

    /**
     * Portal map builder for to create a map of all portals in the game
     * @param portals   the returned constructed map of portals
     * @param colors    sequence of portal colors seen in XML
     * @param locations the list of all portal locations
     */
    protected void makePortals(
            HashMap<HashLocation, Portal> portals,
            ArrayList<String> colors,
            ArrayList<Location> locations
    ) {
        assert(colors.size() == locations.size());

        // using the colors and locations, construct a hashmap of portals paired by key color
        HashMap<String, ArrayList<Portal>> portalsMap = new HashMap<>();
        for (int i=0; i < colors.size(); i++) {
            String color = colors.get(i);
            Location location = locations.get(i);

            // For each color, we want to get the associated sprite and store it
            ArrayList<Portal> currList;
            if (! portalsMap.containsKey(color)) {
                currList = new ArrayList<>();
                portalsMap.put(color, currList);
            }
            else currList = portalsMap.get(color);

            // get the resulting sprite of the color, add that to a new portal
            String sprite = Portal.PortalType.of(color).getColorSprite();
            currList.add(new Portal(sprite, location));
        }

        // constructing the portals map accordingly
        for (Map.Entry<String, ArrayList<Portal>> entry : portalsMap.entrySet()) {
            ArrayList<Portal> pair = entry.getValue();
            assert pair != null && pair.size() > 0;
            Portal p1 = pair.get(0);
            Portal p2 = pair.get(1);
            p1.setPortalPair(p2);
            HashLocation.put(portals, p1.getStaticLocation(), p1);
            HashLocation.put(portals, p2.getStaticLocation(), p2);
        }
    }

    /**
     * Creates instance of PortalFactory for portal creation
     * @return  PortalFactory instance
     */
    protected static synchronized PortalFactory getInstance() {
        if (instance == null)
            instance = new PortalFactory();
        return instance;
    }
}