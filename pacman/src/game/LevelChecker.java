package game;

import ch.aplu.jgamegrid.Location;
import game.utility.GameCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


/**
 * The level checker class to check for level validity. This will dictate whether the gameplay mode
 * or the editor mode will be initiated by the Controller.
 * @see editor.Controller
 */
public class LevelChecker {
    private static final int MIN_NUM_MANDATORY = 2;
    private static final int MAX_NUM_PORTAL_PAIR = 2;

    private final PathFinder pathFinder = new PathFinder();
    private final GameCallback gameCallback;
    private String xmlFile;

    /**
     * LevelChecker constructor. Requires game callback to report for errors.
     * @param gameCallback the game callback
     */
    public LevelChecker(GameCallback gameCallback) {
        this.gameCallback = gameCallback;
    }

    /**
     * Set the XML file path for the current level. It will only be used for updating to callback,
     * so it will only retain the file name, and not any path to it.
     * @param xmlFile the XML file path
     */
    public void setXmlFile(String xmlFile) {
        String[] deliminators = new String[]{"\\", "/"};
        for (String deliminator : deliminators) {
            int index = xmlFile.lastIndexOf(deliminator);
            this.xmlFile = xmlFile.substring(index + 1);
        }
    }


    /**
     * Check for the validity of the number of mandatory items. Specifically, there must be at least
     * 2 items present for the level checking to succeed.
     * @param game the game
     * @return     True if valid, False if not
     */
    private boolean numItemsValid(Game game) {
        HashMap<HashLocation, Item> mandatoryItems = game.getManager().getMandatoryItems();
        boolean valid = mandatoryItems.size() >= MIN_NUM_MANDATORY;
        if (! valid) {
            String error = String.format("[Level %s – less than 2 Gold and Pill]", xmlFile);
            gameCallback.writeString(error);
        }
        return valid;
    }

    /**
     * Get the unreachable mandatory items. Mandatory items are items that must be obtained for the
     * player to win. If there are any unreachable mandatory items, the game will not be initiated
     * and instead the editor mode will be. This is all part of Controller's behaviors.
     * @param game the game
     * @return     the map of unreachable items
     */
    private HashMap<HashLocation, Item> unreachableItems(Game game) {
        // relevant objects and instantiations
        PacActor pacActor = game.getManager().getPacActor();
        HashMap<HashLocation, Item> items = game.getManager().getMandatoryItems();
        ArrayList<Location> reachable = pathFinder.dfsGreedyCheck(pacActor);
        HashMap<HashLocation, Item> reachableMap = new HashMap<>();

        // get the reachable hash map
        for (Location loc : reachable) {
            if (HashLocation.contain(items, loc))
                HashLocation.put(reachableMap, loc, HashLocation.get(items, loc));
        }

        // get unreachable hash map
        HashMap<HashLocation, Item> unreachable = new HashMap<>();
        for (HashLocation hashLoc : items.keySet()) {
            if (! reachableMap.containsKey(hashLoc))
                unreachable.put(hashLoc, items.get(hashLoc));
        }
        return unreachable;
    }


    /**
     * Check if all mandatory items are reachable or not in the game.
     * @param game the game
     * @return     True if all mandatory items are reachable, and False if otherwise
     */
    private boolean reachableMandatoryItems(Game game) {
        HashMap<HashLocation, Item> unreachable = unreachableItems(game);
        boolean isReachable = unreachable.size() == 0;

        // separate gold and pill
        HashMap<HashLocation, Gold> unGold = new HashMap<>();
        HashMap<HashLocation, Pill> unPill = new HashMap<>();
        for (Map.Entry<HashLocation, Item> entry : unreachable.entrySet()) {
            if (entry.getValue() instanceof Gold) unGold.put(entry.getKey(), (Gold) entry.getValue());
            else unPill.put(entry.getKey(), (Pill) entry.getValue());
        }

        // sort gold and pill hashmaps
        TreeMap<HashLocation, Item> sortedGold = new TreeMap<>(unGold);
        TreeMap<HashLocation, Item> sortedPill = new TreeMap<>(unPill);

        // get the gold string to be put to callback's log
        boolean goldReachBool = true;
        StringBuilder goldString =
                new StringBuilder(String.format("[Level %s – %s not accessible:", xmlFile, Gold.class));
        for (Map.Entry<HashLocation, Item> entry : sortedGold.entrySet()) {
            goldReachBool = false;
            goldString.append(String.format(" (%d,%d);",
                    entry.getValue().getX(), entry.getValue().getY()));
        }
        goldString.deleteCharAt(goldString.length() - 1);
        goldString.append("]");
        if (! goldReachBool)
            gameCallback.writeString(String.valueOf(goldString));

        // get the pill string to be put to callback's log
        boolean pillReachBool = true;
        StringBuilder pillString =
                new StringBuilder(String.format("[Level %s – %s not accessible:", xmlFile, Pill.class));
        for (Map.Entry<HashLocation, Item> entry : sortedPill.entrySet()) {
            pillReachBool = false;
            pillString.append(String.format(" (%d,%d);",
                    entry.getValue().getX(), entry.getValue().getY()));
        }
        pillString.deleteCharAt(pillString.length() - 1);
        pillString.append("]");
        if (! pillReachBool)
            gameCallback.writeString(String.valueOf(pillString));
        return isReachable;
    }


    /**
     * Check for pacman actor's validity. Specifically, it will check if there is not yet a pacman
     * actor put to the game, or if there are more than 1.
     * @param game the game
     * @return     True if valid, False if not
     */
    private boolean pacActorValid(Game game) {
        // no PacActor on grid
        ArrayList<Location> locations = game.getManager().getPacActorLocations();
        if (locations.size() == 0) {
            String error = String.format("[Level %s – no start for PacMan]", xmlFile);
            gameCallback.writeString(String.valueOf(error));
            return false;
        }
        // more than 1 PacActor on grid
        else if (locations.size() > 1) {
            StringBuilder errorBuilder =
                    new StringBuilder(String.format("[Level %s – more than one start for Pacman:", xmlFile));
            for (Location location : locations)
                errorBuilder.append(String.format(" (%d,%d);", location.getX(), location.getY()));
            errorBuilder.deleteCharAt(errorBuilder.length() - 1);
            errorBuilder.append("]");
            gameCallback.writeString(String.valueOf(errorBuilder));
            return false;
        }
        return true;
    }


    /**
     * Check for the validity of portals. Specifically, it will check if portals are paired up
     * correctly (each portal must be paired with 1 and only 1 other portal, each pair can only
     * use up a single portal color).
     * @param game the game
     * @return     True if valid, False if not
     */
    private boolean portalsValid(Game game) {
        boolean valid = true;

        // get a list, of list of portal pairs
        HashMap<HashLocation, Portal> portals = game.getManager().getPortals();
        ArrayList<Portal> whitePortals  = new ArrayList<>();
        ArrayList<Portal> yellowPortals = new ArrayList<>();
        ArrayList<Portal> goldPortals   = new ArrayList<>();
        ArrayList<Portal> greyPortals   = new ArrayList<>();
        ArrayList<ArrayList<Portal>> allPortals = new ArrayList<>();
        allPortals.add(whitePortals);
        allPortals.add(yellowPortals);
        allPortals.add(goldPortals);
        allPortals.add(greyPortals);

        // initialize the list of pairs
        for (Map.Entry<HashLocation, Portal> entry : portals.entrySet()) {
            if (entry.getValue().getColor() == Portal.PortalColor.White)
                whitePortals.add(entry.getValue());
            else if (entry.getValue().getColor() == Portal.PortalColor.Yellow)
                yellowPortals.add(entry.getValue());
            else if (entry.getValue().getColor() == Portal.PortalColor.DarkGold)
                goldPortals.add(entry.getValue());
            else
                greyPortals.add(entry.getValue());
        }

        // for each list of pairs
        for (ArrayList<Portal> portalPairs : allPortals) {
            if (portalPairs.size() > MAX_NUM_PORTAL_PAIR) {
                StringBuilder errorBuilder =
                        new StringBuilder(String.format(
                                "[Level %s – portal %s count is not 2:",
                                xmlFile, portalPairs.get(0).getColor()));
                for (Portal portal : portalPairs)
                    errorBuilder.append(String.format(
                            " (%d,%d);", portal.getStaticLocation().getX(), portal.getStaticLocation().getY()));
                errorBuilder.deleteCharAt(errorBuilder.length() - 1);
                errorBuilder.append("]");
                gameCallback.writeString(String.valueOf(errorBuilder));
                valid = false;
            }
        }
        return valid;
    }

    /**
     * The method used publicly by the Controller to check for level validity of the game.
     * @param game the game
     * @return     True if valid, False if not
     */
    public boolean checkLevel(Game game) {
        // pacman check
        boolean pacActorBool  = pacActorValid(game);
        boolean portalsBool   = portalsValid(game);
        boolean numItemsBool  = numItemsValid(game);
        boolean reachableBool = reachableMandatoryItems(game);
        return pacActorBool && portalsBool && numItemsBool && reachableBool;
    }
}
