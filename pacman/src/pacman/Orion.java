package pacman;

import ch.aplu.jgamegrid.Location;
import pacman.utility.PropertiesLoader;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Orion class extended from abstract parent Monster.
 * Enemies in the game whose movement is based solely on protecting gold pieces.
 * @see Monster
 * @see Gold
 */
public class Orion extends Monster {
    // Name of class required for GameCallback
    private static final MonsterType TYPE = MonsterType.Orion;
    // Constructor arguments
    public static final int numOrionImages = 1;
    public static final String directory = PropertiesLoader.PATH + "m_orion.gif";

    // Constants used to check for non-diagonal directions
    private static final int CHECK_NON_DIAGONAL = 10;
    private static final int NON_DIAGONAL = 0;
    private static final int LIST_START = 0;

    // Variables to keep track of positions of gold pieces for Orion's movement logic
    private HashLocation currDestination = null;
    private boolean hasDestination = false;
    private final HashMap<HashLocation, Boolean> goldVisited = new HashMap<>();
    private final HashMap<HashLocation, Boolean> goldPacmanAte = new HashMap<>();

    /**
     * Orion constructor
     * @param manager stores locations of all game objects
     */
    public Orion(ObjectManager manager) {
        super(manager, false, directory, numOrionImages);
        assert manager != null;
        setType(TYPE);
        // Assert there are actually items for Orion to store
        assert ! this.getManager().getItems().isEmpty();
        this.makeGoldMaps();
    }

    /**
     * Get all the gold pieces that PacMan has eaten. Used to determine Orion's direction since its
     * behaviors are different when not all gold pieces have been eaten.
     * @return the hashmap of gold pieces that pacman has eaten
     */
    public HashMap<HashLocation, Boolean> getGoldPacmanAte() {
        return goldPacmanAte;
    }


    /**
     * The Orion's movement approach in game. Overridden from Monster.
     * <ul>
     *     <li>Moves Orion to its next location, based on walking through every gold location randomly;
     *         prioritizing golds that Pacman has yet to eat.
     *     <li>Orion has walk cycles; a walk cycle starts when Orion determines the first gold location
     *         to walk to, and ends when it arrives at its last unvisited gold location.
     * </ul>
     */
    @Override
    protected Location nextMonsterLocation(int stepSize) {
        Location finalLoc = null;  // This checks if we can move anywhere

        // If already at destination or destination is null, find a new destination to walk to
        if (this.currDestination != null &&
                this.currDestination.location().getX() == this.getLocation().getX() &&
                this.currDestination.location().getY() == this.getLocation().getY())
        {
            this.hasDestination = false;
            this.goldVisited.put(this.currDestination, true);

            // After Orion finishes walk cycle, reset its cycle by setting all goldLocations values to false
            if (this.checkIfAllVisited(new ArrayList<>(this.goldVisited.keySet())))
                goldVisited.replaceAll((l, v) -> false);
        }
        if (!hasDestination) this.findNewGold();

        // Now we go towards the direction of this new location
        Location orionLocation = this.getLocation();

        // Orion monster can only go vertically and horizontally (doesn't fly)
        // Want to go towards direction where distance to gold is minimized
        int minDistance = Integer.MAX_VALUE;
        ArrayList<Location> possibleLocations = new ArrayList<>();
        for (Location.CompassDirection dir : Location.CompassDirection.values()) {
            if (dir.getDirection() % CHECK_NON_DIAGONAL == NON_DIAGONAL) {
                Location currLocation = orionLocation.getAdjacentLocation(dir.getDirection(), stepSize);
                int distanceToGold = currLocation.getDistanceTo(this.currDestination.location());

                // Track visited locations with visited list to prevent going to same 2 locations repeatedly
                if (this.canMove(stepSize) &&
                    this.notVisited(currLocation) && distanceToGold <= minDistance)
                {
                    // Keep track of all possible tying directions
                    if (distanceToGold < minDistance) {
                        minDistance = distanceToGold;
                        possibleLocations = new ArrayList<>();
                    }
                    possibleLocations.add(currLocation);
                }
            }
        }

        // In case every move has been visited already, just find the immediate place you can move to
        if (possibleLocations.isEmpty()) {
            Location.CompassDirection[] possibleDirections = Location.CompassDirection.values();
            ArrayList<Integer> directionValues = new ArrayList<>();
            for (Location.CompassDirection dir : possibleDirections) {
                directionValues.add(dir.getDirection());
            }

            // Keep randomly selecting directions and getting corresponding location
            // until you find a movable location, or you go through the whole list
            while(!directionValues.isEmpty()) {
                int currIndex = this.getRandomizer().nextInt(LIST_START, directionValues.size());
                int currentDir = directionValues.get(currIndex);
                Location newLocation = this.getLocation().getAdjacentLocation(currentDir, stepSize);
                if (this.canMove(stepSize) && currentDir % CHECK_NON_DIAGONAL == NON_DIAGONAL) {
                    finalLoc = newLocation;
                    break;
                }
                directionValues.remove(currIndex);
            }
        }

        // There may be more than one unvisited location that minimizes distance
        // to a gold, randomly select from these options
        else {
            int randomIndex = this.getRandomizer().nextInt(LIST_START, possibleLocations.size());
            finalLoc = possibleLocations.get(randomIndex);
        }

        // Now when the move has been decided, can move Orion to the desired piece
        if (finalLoc != null) this.addVisitedList(finalLoc);
        return finalLoc;
    }


    /**
     * Helper method for moveApproach that decides the next gold piece location Orion moves to.
     */
    private void findNewGold() {
        // keep track of the gold pieces that have and have not been visited
        HashMap<HashLocation, Boolean> notTaken = new HashMap<>();
        for (HashLocation loc : this.goldPacmanAte.keySet())
            // Prioritize un-eaten gold
            if (!this.goldVisited.get(loc))
                HashLocation.put(notTaken, loc.location(), true);

        // If there are still golds pacman hasn't eaten yet and Orion hasn't visited
        ArrayList<HashLocation> goldsToIterate = new ArrayList<>(notTaken.keySet());

        // Otherwise randomly check from all possible gold locations
        if (goldsToIterate.isEmpty() || this.checkIfAllVisited(goldsToIterate)) {
            goldsToIterate = new ArrayList<>(this.goldVisited.keySet());
        }

        // randomly pick which gold to go to, and set new location
        HashLocation newLocation = this.getRandomLocation(goldsToIterate);
        this.hasDestination = true;
        this.currDestination = newLocation;
    }

    /**
     * This method initializes 2 key maps needed for Orion:
     * <ul>
     *     <li>goldLocations: gold piece locations visited for each walking cycle;
     *     <li>goldPacmanAte: gold pieces Pacman ate already
     * </ul>
     */
    private void makeGoldMaps() {
        for (HashLocation loc: this.getManager().getItems().keySet()) {
            if (this.getManager().getItems().get(loc) instanceof Gold) {
                this.goldVisited.put(loc, false);
                this.goldPacmanAte.put(loc, false);
            }
        }
    }


    /**
     * Check if a given list of gold piece locations have been visited by Orion already for a given walk cycle
     * @param golds List of a number (not necessarily all) gold piece locations
     * @return      boolean indicating if all golds in list were visited already
     * @see         HashLocation
     */
    private boolean checkIfAllVisited(ArrayList<HashLocation> golds) {
        for (HashLocation loc : golds)
            if (!this.goldVisited.get(loc))
                return false;
        return true;
    }


    /**
     * Randomly pick a gold location from a given list of gold locations that IS NOT YET VISITED in Orion's
     * walk cycle. To do this, it creates a new list of specific gold pieces where none of said pieces have
     * been visited within a cycle, or is in the same location as the Orion in question.
     * @param golds List of a number (not necessarily all) gold piece locations
     * @return      Random location from list of gold locations
     * @see         HashLocation
     */
    private HashLocation getRandomLocation(ArrayList<HashLocation> golds) {
        ArrayList<HashLocation> goldsToCheck = new ArrayList<>();
        for (HashLocation loc : golds)
            if (! this.goldVisited.get(loc) &&
                (loc.getX() != this.getLocation().getX() || loc.getY() != this.getLocation().getY()))
            {
                goldsToCheck.add(loc);
            }

        // Now return a random location from this new list
        int randomIndex = this.getRandomizer().nextInt(LIST_START, goldsToCheck.size());
        return goldsToCheck.get(randomIndex);
    }
}
