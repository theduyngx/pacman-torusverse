package pacman;
import ch.aplu.jgamegrid.Location;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Factory class for construction of portals; ensures that each portal
 * is paired with each other.
 * @see Portal
 */
public class PortalFactory {
    public static PortalFactory instance;

    /**
     * Constructor Function for all the portals of the game
     * @param colors    Sequence of portal colors seen in XML
     */
    public void makePortals(
            HashMap< String, HashMap<HashLocation, Portal> > portalsMap,
            ArrayList<String> colors,
            ArrayList<Location> locations
    ) {
        assert(colors.size() == locations.size());

        for (int i=0; i < colors.size(); i++) {
            String color = colors.get(i);
            Location loc = locations.get(i);
            // For each color, we want to get the associated sprite
            // and store it
            if (!portalsMap.containsKey(color))
                portalsMap.put(color, new HashMap<>());

            // Get the resulting sprite of the color
            String spriteLoc = Portal.PortalType.of(color).getColorSprite();

            // Now add that sprite into a new portal to make
            HashLocation.put(portalsMap.get(color), loc, new Portal(spriteLoc));
        }
    }

    /**
     * Creates instance of PortalFactory for portal creation
     * @return  PortalFactory instance
     */
    public static synchronized PortalFactory getInstance() {
        if (instance == null)
            instance = new PortalFactory();
        return instance;
    }
}