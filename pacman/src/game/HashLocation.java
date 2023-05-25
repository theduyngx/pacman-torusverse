package game;
import ch.aplu.jgamegrid.Location;
import java.util.Arrays;
import java.util.HashMap;


/**
 * HashLocation record class as a placeholder for Location object, but with purpose-driven hash-ability
 * to ensure that 2 locations that are equal will produce the same hashed value. This is utilized for hash
 * maps that use location as keys for fast accessing.
 * @see Location
 */
public record HashLocation(Location location) {
    // properties
    public int getX() {
        return location.getX();
    }

    public int getY() {
        return location.getY();
    }

    /**
     * Overridden hashing method for the location.
     * @return hashed value
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(getArrayLocation(location));
    }

    /**
     * Overridden equal method.
     * @param obj   the reference object with which to compare
     * @return      whether 2 locations are equal or not
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;
        HashLocation other = (HashLocation) obj;
        return other.getX() == this.getX() && other.getY() == this.getY();
    }

    /**
     * Get the hashable location, which is actually just an array of size 2 in form [x, y].
     * @param location the specified location
     * @return         the hashable array
     */
    private static int[] getArrayLocation(Location location) {
        return new int[]{location.getX(), location.getY()};
    }


    /**
     * Get the value generic given T the HashLocation key.
     * @param map       the hash map
     * @param location  specified location as key
     * @param <T>       the generic type, but in this context we only use live actors and items for it.
     */
    public static <T> T get(HashMap<HashLocation, T> map, Location location) {
        HashLocation hashLocation = new HashLocation(location);
        return map.get(hashLocation);
    }


    /**
     * Put entry to hashmap with HashLocation as key
     * @param map       the hash map
     * @param location  specified location as key
     * @param object    generic object as value
     * @param <T>       the generic type, but in this context we only use live actors and items for it.
     */
    public static <T> void put(HashMap<HashLocation, T> map, Location location, T object) {
        HashLocation hashLocation = new HashLocation(location);
        map.put(hashLocation, object);
    }


    /**
     * Check if hashmap of HashLocation contains a key corresponding to a specified Location object.
     * @param map       the hashmap
     * @param location  specified location
     * @return          boolean value indicating whether key is contained
     * @param <T>       generic Object class
     */
    public static <T> boolean contain(HashMap<HashLocation, T> map, Location location) {
        HashLocation hashLocation = new HashLocation(location);
        return map.containsKey(hashLocation);
    }


    /**
     * Delete an entry from the map, given the location as key.
     * @param map       the hashmap
     * @param location  specified location
     * @return          if there was an entry to be deleted
     * @param <T>       generic Object class
     */
    public static <T> boolean delete(HashMap<HashLocation, T> map, Location location) {
        HashLocation hashLocation = new HashLocation(location);
        boolean ret = map.containsKey(hashLocation);
        map.remove(hashLocation);
        return ret;
    }
}
