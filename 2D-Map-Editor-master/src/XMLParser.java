import pacman.HashableLocation;
import pacman.Item;
import pacman.Monster;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * XML Parser is a class meant to extract all the items, enemies, and pacActor
 * and their locations and store it in a hashmap
 */
public class XMLParser {

    private final ArrayList<Monster> monsters;
    // hashmap of all items with their location as key
    private final HashMap<HashableLocation, Item> items;
    // hashmap of all walls with their location as key
    private final HashMap<HashableLocation, Integer> walls;

    public XMLParser() {
        this.monsters = new ArrayList<>();
        this.items = new HashMap<>();
        this.walls = new HashMap<>();
    }
}
